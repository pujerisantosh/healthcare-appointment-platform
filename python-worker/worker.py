import json
import logging
import psycopg2
from kafka import KafkaConsumer
from datetime import datetime

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Database connection
def get_db_connection():
    return psycopg2.connect(
        host="localhost",
        port=5432,
        database="healthcare_db",
        user="postgres",
        password="root"
    )

def update_appointment_status(appointment_id, status, message):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute(
            "UPDATE appointments SET status = %s, updated_at = %s WHERE id = %s",
            (status, datetime.now(), appointment_id)
        )
        cur.execute(
            "INSERT INTO appointment_logs (appointment_id, previous_status, new_status, message, changed_at) VALUES (%s, %s, %s, %s, %s)",
            (appointment_id, None, status, message, datetime.now())
        )
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"Updated appointment {appointment_id} to {status}")
    except Exception as e:
        logger.error(f"DB error: {e}")

def process_booking_event(data):
    logger.info(f"🔔 Sending booking notification to patient: {data.get('patient')}")
    logger.info(f"📅 Appointment with Dr. {data.get('doctor')} at {data.get('time')}")
    logger.info(f"✅ Notification sent successfully!")
    update_appointment_status(data.get('appointmentId'), 'PROCESSING', 'Notification sent to patient')

def process_cancellation_event(data):
    logger.info(f"❌ Cancellation notification for appointment: {data.get('appointmentId')}")
    logger.info(f"📅 Dr. {data.get('doctor')} slot at {data.get('time')} is now free")
    logger.info(f"✅ Cancellation notification sent!")

def main():
    logger.info("🐍 Python Worker starting...")
    
    consumer = KafkaConsumer(
        'appointment-booked',
        'appointment-cancelled',
        bootstrap_servers=['localhost:9092'],
        auto_offset_reset='earliest',
        enable_auto_commit=True,
        group_id='python-healthcare-worker',
        value_deserializer=lambda x: json.loads(x.decode('utf-8'))
    )

    logger.info("✅ Connected to Kafka! Listening for events...")

    for message in consumer:
        topic = message.topic
        data = message.value
        logger.info(f"📡 Received event from topic: {topic}")
        logger.info(f"📦 Data: {data}")

        if topic == 'appointment-booked':
            process_booking_event(data)
        elif topic == 'appointment-cancelled':
            process_cancellation_event(data)

if __name__ == "__main__":
    main()