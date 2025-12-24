# Amazon Sales Big Data Streaming Pipeline

## 📌 Project Overview
This project implements an **end-to-end Big Data streaming pipeline** for real-time analysis of e-commerce sales data.  
The system is designed to handle **high-volume, high-velocity data streams** using modern Big Data technologies.

The pipeline ingests sales data, processes it in real time, applies analytical and probabilistic techniques, and stores the results for further querying and visualization.

---

## 🎯 Problem Statement
Traditional batch-based systems are not suitable for real-time analytics on continuously generated sales data.  
Delayed processing leads to:
- Late detection of trending and new products
- Inefficient inventory management
- Slower business decision-making

This project addresses these challenges by building a **scalable real-time streaming architecture**.

---

## 💡 Solution
We designed a **real-time Big Data pipeline** that:
- Streams sales data continuously
- Processes data in real time
- Optimizes performance using probabilistic data structures
- Stores analytical results efficiently

## 🏗️ System Architecture

```mermaid
flowchart LR
    A["CSV File (Amazon Sales Dataset)"]
    B["Kafka Producer (Python)"]
    C["Kafka Topic (sales-topic)"]
    D["Spark Structured Streaming"]
    E["Probabilistic Analytics: Bloom Filter / Top-K"]
    F["MongoDB: sales_clean | sales_new_products | sales_stats"]
    G["Queries / Dashboard"]

    A --> B --> C --> D --> E --> F --> G
```



## 🛠️ Technologies Used
- **Apache Kafka**
  - Real-time data ingestion and message streaming
- **Apache Spark Structured Streaming**
  - Real-time data processing and analytics
- **Probabilistic Data Structures**
  - Bloom Filter
  - Top Products Tracker (Count-Min based)
  - Approximate Unique Counter
- **MongoDB**
  - Storage for processed data and analytics results
- **Python**
  - Kafka producer implementation

---

## ⚙️ Implementation Details

### Kafka Producer
- Reads sales data from a CSV file
- Converts each record into JSON
- Streams records incrementally to Kafka to simulate real-time data

### Spark Processing
- Consumes data from Kafka using Structured Streaming
- Parses JSON into structured DataFrames
- Cleans and filters data
- Performs aggregations using Spark SQL
- Applies probabilistic data structures for optimization

## 🗄️ MongoDB Queries & Results

Detailed MongoDB queries, aggregations, and result explanations
(with screenshots) are available in the following document:

📄 **MongoDB Queries & Results Explanation**  
👉 [`docs/MongoDB_Queries_Results_Explanation.docx`](docs/MongoDB_Queries_Results_Explanation.docx)

This document demonstrates:
- Clean streaming data stored in MongoDB
- New product detection using Bloom Filter
- Approximate top products from streaming statistics
- Exact aggregations using MongoDB pipelines
- Business interpretation of the results

### Data Storage
Processed data and analytics results are stored in MongoDB collections:
- `sales_clean` – cleaned transactional sales data
- `sales_new_products` – newly detected products using Bloom Filter
- `sales_stats` – streaming analytics and approximate statistics

---

## 🚀 Key Features
- Real-time streaming analytics
- Scalable and fault-tolerant architecture
- Memory-efficient analytics using probabilistic methods
- Suitable for large-scale e-commerce systems

---

## 📊 Results
The system provides:
- Detection of new products
- Tracking of top-selling products
- Estimation of unique products
- Real-time analytical insights stored for querying and visualization

---

## 📈 Performance & Optimization
Probabilistic data structures are used to:
- Reduce memory consumption
- Improve processing speed
- Enable scalable real-time analytics

Approximate results are acceptable for business insights where trends and rankings are more important than exact counts.

---

## ⚠️ Challenges
- Kafka setup and configuration
- Streaming data consistency
- Schema handling for semi-structured data

These challenges were addressed through incremental testing and structured streaming design.

---

## 🔮 Future Work
- Integrate real-time dashboards
- Add machine learning for sales prediction
- Deploy the system on a distributed cluster

---

## 👥 Team Members
- Ahmad Derieh  
- Mohammad Abu Zahid  
- Mohammad Shawahni  
- Mahmood Jawabrh  
