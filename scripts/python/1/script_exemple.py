import random
from datetime import datetime, timedelta

first_names = ["Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack"]
last_names = ["Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Martinez", "Wilson"]

def generate_random_user():
    first_name = random.choice(first_names)
    last_name = random.choice(last_names)
    age = random.randint(18, 80)
    email = f"{first_name.lower()}.{last_name.lower()}@example.com"
    join_date = datetime.now() - timedelta(days=random.randint(1, 1000))
    is_active = random.choice([True, False])
    return {
        "first_name": first_name,
        "last_name": last_name,
        "age": age,
        "email": email,
        "join_date": join_date,
        "is_active": is_active,
    }

def generate_user_list(num_users=50):
    return [generate_random_user() for _ in range(num_users)]

def filter_active_users(users):
    return [user for user in users if user["is_active"]]

def filter_by_age(users, min_age=30):
    return [user for user in users if user["age"] >= min_age]

def calculate_average_age(users):
    if not users:
        return 0
    total_age = sum(user["age"] for user in users)
    return total_age / len(users)

def sort_users_by_name(users):
    return sorted(users, key=lambda x: (x["last_name"], x["first_name"]))

def sort_users_by_join_date(users, reverse=False):
    return sorted(users, key=lambda x: x["join_date"], reverse=reverse)

users = generate_user_list()
active_users = filter_active_users(users)
users_over_30 = filter_by_age(users, 30)
average_age = calculate_average_age(users)
sorted_by_name = sort_users_by_name(users)
sorted_by_join_date = sort_users_by_join_date(users)
