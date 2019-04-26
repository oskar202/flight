## Swagger UI
http://localhost:8080/swagger-ui.html#/
#
* HTTP API endpoint that takes as input two IATA airport codes and provides as output a route between these two airports

* https://openflights.org/data.html <- origin of routes and airports

* Request example:
```json
{
  "end": "MAD",
  "start": "TLL"
}
```
* Response example:
```json
{
  "path": [
    {
      "id": "TLL",
      "name": "TLL"
    },
    {
      "id": "FRA",
      "name": "FRA"
    },
    {
      "id": "MAD",
      "name": "MAD"
    }
  ],
  "distance": 2888.6279416222214,
  "alternativePaths": {
    "[TLL, FRA, ECV]": 2909.0339591637703,
    "[TLL, FRA, TOJ]": 2880.436131269422
  }
}
```
Alternative routes have different final airport within 100 km.
