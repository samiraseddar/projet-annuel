from PIL import Image
import sys
import os
import time

def convert_to_black_and_white(input_image_path, output_directory):
    # Ouvrir l'image d'entrée
    try:
        image = Image.open(input_image_path)
    except Exception as e:
        print(f"Erreur lors de l'ouverture de l'image : {e}")
        return

    # Convertir l'image en noir et blanc
    bw_image = image.convert("L")

    # Générer un nom de fichier de sortie
    base_name = os.path.basename(input_image_path)
    output_file_name = os.path.splitext(base_name)[0] + "_bw.png"
    output_path = os.path.join(output_directory, output_file_name)

    # Sauvegarder l'image transformée
    try:
        bw_image.save(output_path)
        print(f"Image convertie et sauvegardée à : {output_path}")
    except Exception as e:
        print(f"Erreur lors de la sauvegarde de l'image : {e}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python convert_to_bw.py <input_image_path> <output_directory>")
        sys.exit(1)

    input_image_path = sys.argv[1]
    output_directory = sys.argv[2]
    
    time.sleep(10)

    # Appeler la fonction pour convertir l'image
    convert_to_black_and_white(input_image_path, output_directory)