# Eiscp-rest

eiscp-rest is a spring boot application that allows you to control your Onkyo receiver over REST

Currently supports the following features
  - Get and control power state
  - Get and control master volume
  - Query and switch input

### Rest endpoints
### Power
```
GET /power
POST /power/on
POST /power/off
POST /power/toggle
```
### Volume
```
GET /volume
POST /volume/{newVolume}
POST /volume/increase
POST /volume/decrease
GET /volume/is-muted
POST /volume/toggle-mute
```
### Input selection
```
GET /input
POST /input/next
POST /input/previous
POST /input/{newInput}  # I.e. PC or STRM_BOX
GET /input/available
```
### Current state
Fetch or request new states. I.e volume, power, input etc.
```
GET /state
POST /state
```
