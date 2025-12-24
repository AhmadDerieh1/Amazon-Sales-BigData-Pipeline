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

---

## 🏗️ System Architecture
flowchart TD
    A[CSV File<br/>(Amazon Sales Dataset)]
    B[Kafka Producer<br/>(Python)]
    C[Kafka Topic<br/>(sales-topic)]
    D[Spark Structured Streaming]
    E[Probabilistic Analytics<br/>Bloom Filter / Top-K]
    F[MongoDB<br/>sales_clean<br/>sales_new_products<br/>sales_stats]
    G[Queries / Dashboard]

    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    F --> G

---

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

### Data Storage
Processed data and analytics results are stored in MongoDB collections:
- `sales_transactions`
- `sales_stream_metrics`
- `sales_new_products`

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

---

## 📄 License
This project is for academic and educational purposes.
