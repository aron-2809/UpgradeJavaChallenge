import requests

#Request A GET
response = requests.get("http://localhost:9090/api/campsite/find?id=1")
request_1 = response.json()
print("User A get:", request_1)

# #Request B GET
# response = requests.get("http://localhost:9090/api/campsite/find?id=1")
# request_2 = response.json()
# print("User B get:", request_2)
#
# #Request B Update
# response = requests.put("http://localhost:9090/campsite/modify?id=1&arrivalDate=2019-12-14&departureDate=2019-12-15", json = request_2)
# print("User B save:", response.json())
#
# #Request A Update (Blows up on a lock exception)
# response = requests.put("http://localhost:9090/campsite/modify?id=2&arrivalDate=2019-12-17&departureDate=2019-12-18", json = request_1)
# print("User A save:", response.json())
