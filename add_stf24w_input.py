import re

with open('src/main/resources/wa-task-initiation-ia-asylum.dmn', 'r') as f:
    content = f.read()

counter = 0
result_parts = []
last_end = 0

for m in re.finditer(r'<rule id="([^"]+)">', content):
    rule_start = m.start()
    rule_id = m.group(1)

    result_parts.append(content[last_end:rule_start])

    rule_end_match = re.search(r'</rule>', content[rule_start:])
    rule_end = rule_start + rule_end_match.end()
    rule_text = content[rule_start:rule_end]

    counter += 1
    value = 'not("Yes")' if rule_id == 'DecisionRule_001rn0b' else ''

    new_entry = (
        '        </inputEntry>\n'
        f'        <inputEntry id="UnaryTests_stf24w_{counter:04d}">\n'
        f'          <text>{value}</text>\n'
        '        </inputEntry>\n'
        '        <outputEntry'
    )

    modified_rule = re.sub(
        r'        </inputEntry>\n        <outputEntry',
        new_entry,
        rule_text,
        count=1
    )
    result_parts.append(modified_rule)
    last_end = rule_end

result_parts.append(content[last_end:])
result = ''.join(result_parts)

with open('src/main/resources/wa-task-initiation-ia-asylum.dmn', 'w') as f:
    f.write(result)

print(f"Done. Added inputEntry to {counter} rules.")
