
### Test Dataset
http://www.spc.noaa.gov/wcm/#data

### Create CSV file with only 4 columns - year, state, lat, lon

`cut -d, -f 2,8,17,18 1950-2016_all_tornadoes.csv > 1950-2016_all_tornadoes-small.csv`

Run project `appWeb` to start example tile server.

`http://localhost:8080/map`

