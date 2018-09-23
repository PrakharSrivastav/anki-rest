# Anki-rest
To start run below steps
- sudo sysctl -w vm.max_map_count=262144 (so that elasticsearch container does not die)
- docker-compose up -d

this exposes port 19200 on ES container. Run the application directly from IDE and it will connect.

## Test against file 
Uncomment the corresponding section in FinalRoute.java

## Usage
The application demonstrates different ways to achieve same/similar results on the ELK stack. However, each step below shows a gradual evolution from simple monolith application to a more distributed, scalable and intelligent stack capable of providing more insights on the data.


### Camel-ELK stack combination
- In Application.java, uncomment Camel-ELK stack Integration routes
- Monolith version : 
    - Streams the mocked car events from files.
    - Minimal version to show visualizations for the events.
    - Not scalable or distributed. Each and every component involved is single point of failure.
    
### Camel-Kafka-ELK combination
- In Application.java comment other routes and uncomment Camel-Kafka-ELK routes.
- Using Kafka like a MessageQueue. More scalable and distributed.
    - Camel streams the mocked car events to kafka TOPIC (CAR_EVENTS). 
    - Kafka consumers to read events and send visualization data to ELK Stack
    
### Camel-[Kafka+Ksql]-ELK combination
- Setup same. Except run below commands on ksql
    - `docker exec -it ksql-cli /bin/sh `
    - `ksql http://ksql-server:8088`
    - `CREATE STREAM disaster_recovery_stream (type VARCHAR, carId VARCHAR, deviceId VARCHAR, lastKnownTrack INTEGER, carName VARCHAR,  dateTime INTEGER,  lap INTEGER,raceStatus VARCHAR , raceId VARCHAR,demozone VARCHAR) WITH (VALUE_FORMAT = 'JSON', KAFKA_TOPIC = 'CAR_EVENTS');`
    - `create stream disaster_recovery as select * from disaster_recovery_stream where type = 'VEHICLE_DELOCALIZED';`
- Using existing pub/sub structure for ELK visualizations
- Using ksql, creating streams for Vehicle_Delocalized events
- Perform the slack notifications on every new event


### To do
- Windowed streaming to analyse more events
- Identify more use cases
    - Accident during 
