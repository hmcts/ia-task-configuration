#!/usr/bin/env bash

set -eu

# Default values
PR_NUMBER=""
DRY_RUN=false
WORKSPACE=$(pwd)
TENANT_ID="ia"
PRODUCT="ia"

# Usage function
usage() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Upload Work Allocation DMN files to Camunda in preview environment"
    echo ""
    echo "Options:"
    echo "  -p, --pr PR_NUMBER      PR number for preview environment (required)"
    echo "  -d, --dry-run           Show what would be done without actually uploading"
    echo "  -w, --workspace PATH    Workspace path (default: current directory)"
    echo "  -t, --tenant-id ID      Tenant ID (default: ia)"
    echo "  -h, --help              Show this help message"
    echo ""
    echo "Example:"
    echo "  $0 -p 2609"
    echo "  $0 -p 2609 --dry-run"
    exit 0
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -p|--pr)
            PR_NUMBER="$2"
            shift
            shift
            ;;
        -d|--dry-run)
            DRY_RUN=true
            shift
            ;;
        -w|--workspace)
            WORKSPACE="$2"
            shift
            shift
            ;;
        -t|--tenant-id)
            TENANT_ID="$2"
            shift
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use -h or --help for usage information"
            exit 1
            ;;
    esac
done

# Validate required parameters
if [[ -z "${PR_NUMBER}" ]]; then
    echo "Error: PR number (-p, --pr) is required"
    echo "Use -h or --help for usage information"
    exit 1
fi

# Validate PR number is numeric
if ! [[ "${PR_NUMBER}" =~ ^[0-9]+$ ]]; then
    echo "Error: PR number must be numeric"
    exit 1
fi

# Set environment variables
CAMUNDA_BASE_URL="https://camunda-ia-case-api-pr-${PR_NUMBER}.preview.platform.hmcts.net"
DMN_FILEPATH="${WORKSPACE}/src/main/resources"
S2S_SECRET=${IAC_S2S_KEY:-AABBCCDDEEFFGGHH}

echo "=========================================="
echo "Work Allocation DMN Upload to Preview"
echo "=========================================="
echo "PR Number: ${PR_NUMBER}"
echo "Camunda URL: ${CAMUNDA_BASE_URL}"
echo "DMN Files Path: ${DMN_FILEPATH}"
echo "Tenant ID: ${TENANT_ID}"
echo "Product: ${PRODUCT}"
echo "Workspace: ${WORKSPACE}"

if [[ "${DRY_RUN}" == "true" ]]; then
    echo "DRY RUN MODE: No files will be uploaded"
fi
echo "=========================================="

# Check if DMN files directory exists
if [[ ! -d "${DMN_FILEPATH}" ]]; then
    echo "Error: DMN files directory not found: ${DMN_FILEPATH}"
    exit 1
fi

# Find DMN files
DMN_FILES=$(find "${DMN_FILEPATH}" -name '*.dmn' -type f)
if [[ -z "${DMN_FILES}" ]]; then
    echo "Error: No DMN files found in ${DMN_FILEPATH}"
    exit 1
fi

echo "Found DMN files:"
echo "${DMN_FILES}" | while read -r file; do
    echo "  - $(basename "${file}")"
done
echo ""

# Exit if dry run
if [[ "${DRY_RUN}" == "true" ]]; then
    echo "Dry run completed successfully"
    exit 0
fi

# Generate service token
echo "Generating service token..."
SERVICE_TOKEN=$(realpath "${WORKSPACE}")/bin/utils/idam-lease-service-token.sh

if [[ -z "${SERVICE_TOKEN}" ]]; then
    echo "Error: Failed to generate service token"
    exit 1
fi

echo "Service token generated successfully"
echo ""

# Upload DMN files
UPLOAD_SUCCESS_COUNT=0
UPLOAD_FAIL_COUNT=0

echo "Starting DMN file upload..."
echo ""

for file in ${DMN_FILES}; do
    FILENAME=$(basename "${file}")
    echo "Uploading: ${FILENAME}..."

    UPLOAD_RESPONSE=$(curl --insecure -v --silent -w "\n%{http_code}" --show-error -X POST \
        "${CAMUNDA_BASE_URL}/engine-rest/deployment/create" \
        -H "Accept: application/json" \
        -H "ServiceAuthorization: Bearer ${SERVICE_TOKEN}" \
        -F "deployment-name=${FILENAME}" \
        -F "deploy-changed-only=true" \
        -F "deployment-source=${PRODUCT}" \
        -F "tenant-id=${TENANT_ID}" \
        -F "file=@${file}" 2>/dev/null || echo -e "\n500")

    UPLOAD_HTTP_CODE=$(echo "${UPLOAD_RESPONSE}" | tail -n1)
    UPLOAD_RESPONSE_CONTENT=$(echo "${UPLOAD_RESPONSE}" | sed '$d')

    if [[ "${UPLOAD_HTTP_CODE}" == "200" ]]; then
        echo "✅ ${FILENAME} uploaded successfully"
        if [[ -n "${UPLOAD_RESPONSE_CONTENT}" && "${UPLOAD_RESPONSE_CONTENT}" != "null" ]]; then
            echo "   Response: ${UPLOAD_RESPONSE_CONTENT}"
        fi
        ((UPLOAD_SUCCESS_COUNT++))
    else
        echo "❌ ${FILENAME} upload failed (HTTP ${UPLOAD_HTTP_CODE})"
        if [[ -n "${UPLOAD_RESPONSE_CONTENT}" && "${UPLOAD_RESPONSE_CONTENT}" != "null" ]]; then
            echo "   Error: ${UPLOAD_RESPONSE_CONTENT}"
        fi
        ((UPLOAD_FAIL_COUNT++))
    fi
    echo ""
done

# Summary
echo "=========================================="
echo "Upload Summary"
echo "=========================================="
echo "Total files processed: $((UPLOAD_SUCCESS_COUNT + UPLOAD_FAIL_COUNT))"
echo "Successful uploads: ${UPLOAD_SUCCESS_COUNT}"
echo "Failed uploads: ${UPLOAD_FAIL_COUNT}"
echo "=========================================="

# Exit with appropriate code
if [[ ${UPLOAD_FAIL_COUNT} -gt 0 ]]; then
    echo "❌ Upload completed with ${UPLOAD_FAIL_COUNT} failure(s)"
    exit 1
else
    echo "✅ All DMN files uploaded successfully!"
    exit 0
fi
