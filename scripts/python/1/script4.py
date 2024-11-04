import sys
import time

if len(sys.argv) < 3:
    print("Usage: python AutreScript.py <input_file> <output_file>")
    sys.exit(1)

input_file_path = sys.argv[1]
output_file_path = sys.argv[2]

time.sleep(10)

with open(input_file_path, 'r') as file:
    data = file.read()

output_data = data.lower()
with open(output_file_path, 'w') as file:
    file.write(output_data)

print(f'Output written to {output_file_path}')