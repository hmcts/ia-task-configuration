# Work Allocation System - IA Task Configuration

## Overview

The Work Allocation system is an automated task assignment platform that replaces email-based task management for internal users. It integrates with CCD (Case and Case Data) to automatically create, assign, and manage tasks based on configurable business rules.

## Purpose

- **Eliminates email communications** for case progress notifications
- **Automates task assignment** from shared inboxes
- **Categorizes tasks** by type (review appeals, listing cases, judge assignment)
- **Provides role-based assignment** to appropriate user types (legal officers, admin staff, CTSC staff)

## System Architecture

### Message Flow
1. **CCD Event triggered** → Message sent to CCD queue
2. **Work Allocation subscribes** to messages based on publish flags
3. **Tasks created** according to DMN (Decision Model and Notation) rules
4. **XUI displays** tasks to users

### Key Components

#### User Interface
The system provides two main tabs:

- **My Work**: Tasks assigned to or available for the current user
- **All Work**: Overview for supervisors/managers to see all tasks and assign them

#### Filtering Options
- **My Work filters**: Location, work type, role category
- **All Work filters**: Additional task name filter
- **Predefined filters**: Cannot be customized without XUI changes

## Technical Configuration

### DMN Files Structure

The system uses six main DMN (Decision Model and Notation) files:

#### 1. Task Initiation DMN
- Defines **when** tasks are created
- Based on CCD events and conditions
- Uses case data fields for business rules
- Supports delay mechanisms for follow-up tasks

#### 2. Configuration DMN
- Defines task attributes (work type, role category)
- Configures shortcuts to events
- Sets up due date calculations
- Manages priority settings
- Controls UI columns and filters

#### 3. Permissions DMN
- Controls user access to tasks
- Defines task-level permissions (assign, cancel, complete)
- Implements role-based access control

#### 4. Task Types DMN
- Lists all task types for filtering in UI
- Must match task names defined in initiation file

#### 5. Task Completion DMN
- Defines auto-completion rules
- Automatically closes tasks when events progress

#### 6. Cancellation DMN
- Cancels follow-up tasks when actions complete early
- Uses process categories for bulk cancellation

### CCD Integration

#### Publish Flags
- Control message sending at both event and field level
- Must be set to `Y` for events and fields that should trigger work allocation
- Case data fields used in DMN rules must have publish flags enabled

#### Task Lifecycle
1. **Creation**: Based on event triggers and configured conditions
2. **Assignment**: Manual or automatic based on permissions
3. **Completion**: Manual by users or automatic based on event progression
4. **Cancellation**: Automatic for outdated follow-up tasks

## Building and Deploying the Application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

```bash
./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
./gradlew assemble
```

Create docker image:

```bash
docker-compose build
```

Run the distribution by executing the following command:

```bash
docker-compose up
```

This will start the API container exposing the application's port
(set to `4550` in this template app).

In order to test if the application is up, you can call its health endpoint:

```bash
curl http://localhost:4550/health
```

### Alternative script to run application

To skip all the setting up and building, just execute the following command:

```bash
./bin/run-in-docker.sh
```

For more information:

```bash
./bin/run-in-docker.sh -h
```

## Development Workflow

### Making Changes

To add new tasks or modify existing ones:

1. **Update Task Initiation DMN** - Add rules for when tasks should be created
2. **Configure attributes in Configuration DMN** - Set filters, shortcuts, due dates
3. **Set permissions in Permissions DMN** - Define who can access the tasks
4. **Add task type to Task Types DMN** - Enable filtering in UI
5. **Configure completion rules** (if needed) - Set auto-completion conditions
6. **Update unit tests** - Ensure test coverage for new rules

### Repository Structure
- **Task Configuration Repo**: Contains all DMN files
- **IA Case API**: Consumes DMN files via automated scripts
- **Branch selection**: Configurable for DMN loading

## Testing Strategy

### Unit Tests
- Each DMN file has dedicated unit tests
- Tests validate rule logic using input/output mocks
- Tests are maintained and should be updated with rule changes

### Functional Tests
- **Current status**: Outdated and failing
- **Recommendation**: Write new isolated tests for current work
- **Strategy**: Avoid dependency on legacy test suite

### Manual Testing
- **Preview Environment**: Primary platform for testing changes
- **Camunda Cockpit**: Available at `/camunda` path for manual DMN deployment
- **Local Environment**: Supported but requires message queue configuration

## Environment Setup

### Preview Environment
- Preferred for integration testing
- Manual DMN upload via Camunda UI using admin credentials
- Configuration details available in repository README

### Local Development
- Full support available
- Requires Azure Service Bus message queue setup
- Environment configuration needed for CCD integration

## Known Limitations and Challenges

### Development Constraints
- **DMN file conflicts**: Cannot merge parallel changes due to unique ID regeneration
- **Limited UI customization**: Filters and columns are predefined in XUI
- **Testing complexity**: Functional tests currently unreliable

### Technical Challenges
- **Camunda modeler**: Regenerates unique IDs, complicating parallel development
- **Environment secrets**: Preview configuration sharing requires careful handling
- **Pipeline integration**: Not enabled due to work allocation team concerns

## Current Implementation Examples

### Detained Appeals
- Adding "Detained" prefix to existing task types
- Requires rule duplication for detained vs non-detained cases
- Significant configuration effort due to system limitations
- Alternative solutions (case category prefix) rejected by service requirements

## Best Practices

### Rule Configuration
- Use `processCategory` for logical task grouping and bulk cancellations
- Implement shortcuts for direct navigation to related UI events
- Configure appropriate delays for follow-up tasks
- Ensure publish flags are correctly set in CCD definitions

### Development Process
- Test changes in preview environment before deployment
- Maintain unit test coverage for all DMN rules
- Coordinate with team to avoid file merge conflicts
- Document rule logic and business requirements

## Support and Maintenance

### Monitoring
- **Camunda Cockpit**: Available for monitoring task execution and troubleshooting
- **Message Queue**: Monitor CCD event processing and task creation

### Troubleshooting
- Check publish flags in CCD configuration
- Verify DMN rule conditions and outputs
- Review Camunda logs for execution errors
- Validate case data availability for rule evaluation

## Getting Started

For new developers working with the Work Allocation system:

1. **Familiarize yourself** with DMN notation and Camunda concepts
2. **Set up preview environment** access for testing
3. **Review existing DMN files** to understand current rule patterns
4. **Run unit tests** to understand expected inputs and outputs
5. **Practice manual testing** using Camunda cockpit

For questions or support, refer to the team documentation or contact the Work Allocation development team.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

