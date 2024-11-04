from PIL import Image, ImageFilter
import sys
import os
import random
import time

def apply_weird_transformations(input_image_path, output_directory):
    # Ouvrir l'image d'entrée
    try:
        image = Image.open(input_image_path)
    except Exception as e:
        print(f"Erreur lors de l'ouverture de l'image : {e}")
        return
    

    # Appliquer des transformations bizarres
    transformations = [
        lambda img: img.transpose(Image.FLIP_LEFT_RIGHT),  # Flip horizontal
        lambda img: img.transpose(Image.FLIP_TOP_BOTTOM),  # Flip vertical
        lambda img: img.rotate(90),  # Rotation 90 degrés
        lambda img: img.rotate(180),  # Rotation 180 degrés
        lambda img: img.rotate(270),  # Rotation 270 degrés
        lambda img: img.filter(ImageFilter.GaussianBlur(5)),  # Flou
        lambda img: img.filter(ImageFilter.CONTOUR),  # Contours
    ]

    # Appliquer une transformation aléatoire
    transformation = random.choice(transformations)
    transformed_image = transformation(image)

    # Générer un nom de fichier de sortie
    base_name = os.path.basename(input_image_path)
    output_file_name = os.path.splitext(base_name)[0] + "_weird.png"
    output_path = os.path.join(output_directory, output_file_name)
    
    time.sleep(10)

    # Sauvegarder l'image transformée
    try:
        transformed_image.save(output_path)
        print(f"Image transformée et sauvegardée à : {output_path}")
    except Exception as e:
        print(f"Erreur lors de la sauvegarde de l'image : {e}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python weird_transformations.py <input_image_path> <output_directory>")
        sys.exit(1)

    input_image_path = sys.argv[1]
    output_directory = sys.argv[2]

    # Appeler la fonction pour appliquer les transformations
    apply_weird_transformations(input_image_path, output_directory)
