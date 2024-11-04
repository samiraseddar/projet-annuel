import os
import sys
import time

def transform_text(input_file, output_dir):
    # Vérifier si le fichier d'entrée existe
    if not os.path.isfile(input_file):
        print(f"Le fichier d'entrée '{input_file}' n'existe pas.")
        return

    # Vérifier si le répertoire de sortie existe, sinon le créer
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    try:
        # Lire le contenu du fichier d'entrée
        with open(input_file, 'r') as file:
            lines = file.readlines()
        
        # Appliquer les transformations
        transformed_lines = []
        for i, line in enumerate(reversed(lines), start=1):  # Inverser les lignes
            transformed_line = f"{i}: {line.strip().upper()}"  # Numéroter et mettre en majuscules
            transformed_lines.append(transformed_line)

        # Nom du fichier de sortie
        output_filename = os.path.join(output_dir, "transformed_" + os.path.basename(input_file))
        
        # Écrire les lignes transformées dans le fichier de sortie
        with open(output_filename, 'w') as file:
            file.write("\n".join(transformed_lines))

        print(f"Texte transformé sauvegardé sous : {output_filename}")

    except Exception as e:
        print(f"Erreur lors de la transformation du texte : {e}")

if __name__ == "__main__":
    time.sleep(15)
    if len(sys.argv) != 3:
        print("Utilisation : python transform_text.py <input_file> <output_dir>")
        sys.exit(1)

    # Récupérer les arguments
    input_file = sys.argv[1]
    output_dir = sys.argv[2]

    # Exécuter la transformation
    transform_text(input_file, output_dir)