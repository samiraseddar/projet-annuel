import sys

if len(sys.argv) < 2:
    print("Usage: python MonScript2.py <input_file>")
    sys.exit(1)

input_file_path = sys.argv[1]
with open(input_file_path, 'r') as file:
    data = file.read()

output_data = data.upper()  # Transforme en majuscules
output_file_path = 'output_' + input_file_path
with open(output_file_path, 'w') as file:
    file.write(output_data)

print(f'Output written to {output_file_path}')