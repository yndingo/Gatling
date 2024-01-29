<p align="center">
  <b>Simple load test for puting items to basket in shop</b>
</p>

<b>Actions</b>
1. open main page "fvd.ru" with getting all context
2. search by regex random item to put in basket
3. put selected item to basket

<b>Load test</b>

1. ramp from 0 to 3 users in 10 seconds
2. constant response per second is 2rps during 30 seconds

<b>Results</b>

![Aggregate Report](Results/number of responses per second.png?raw=true "Title")

![Aggregate Report](Results/FVDopen.png?raw=true "Title")

![Aggregate Report](Results/FVDlogin.png?raw=true "Title")

![Aggregate Report](Results/FVDAddItem.png?raw=true "Title")
