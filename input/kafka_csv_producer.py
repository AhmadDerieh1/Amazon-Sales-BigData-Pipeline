import csv, json, time, os
from kafka import KafkaProducer

CSV_PATH = r"C:\Users\ahmad\Desktop\tring big data\final-project\amazon_sales_2025_INR.csv"
TOPIC = "sales-topic"
BOOTSTRAP = "localhost:9092"

print("CSV exists?", os.path.exists(CSV_PATH))
if not os.path.exists(CSV_PATH):
    raise FileNotFoundError(f"CSV not found: {CSV_PATH}")

producer = KafkaProducer(
    bootstrap_servers=BOOTSTRAP,
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode("utf-8")
)

with open(CSV_PATH, "r", encoding="utf-8-sig", newline="") as f:
    reader = csv.DictReader(f)
    for i, row in enumerate(reader, start=1):
        producer.send(TOPIC, row)
        if i % 20 == 0:
            producer.flush()
        print(f"Sent row {i}")
        time.sleep(0.2)

producer.flush()
producer.close()
print("Done streaming CSV -> Kafka")
