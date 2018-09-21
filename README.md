# Anki-rest
To start run below steps
- sudo sysctl -w vm.max_map_count=262144 (so that elasticsearch container does not die)
- docker-compose up -d

this exposes port 19200 on ES container. Run the application directly from IDE and it will connect.

## Test against file 
Uncomment the corresponding section in FinalRoute.java


