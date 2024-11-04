import sys

if len(sys.argv) < 3:
    print("Usage: python MonScript.py <input_file> <output_file>")
    sys.exit(1)

input_file_path = sys.argv[1]
output_file_path = sys.argv[2]

with open(input_file_path, 'r') as file:
    data = file.read()

output_data = data[::-1]  # Inverse le contenu du fichier
with open(output_file_path, 'w') as file:
    file.write(output_data)

print(f'Output written to {output_file_path}')