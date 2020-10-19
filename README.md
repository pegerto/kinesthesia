
<div style="diplay: block;margin-left: auto;margin-right: auto;width: 50%;">
  <img src="https://user-images.githubusercontent.com/261659/96519293-8e65d500-1264-11eb-831f-8750eb1977df.png" alt="Kinesthesia">
</div> 

Kinesthesia simplify the operational complexity of managing a Kafka cluster. 

Kinesthesia, is the sense that lets us perceive the location, movement, and action of parts of the body. This term define the main goal of this project  that allow the user to percive and sense the actions on the cluster simplifying the operations.


# 🚀 Install

This project provides a small scala service and a react web interface. They packed toguether into a docker image. 

```
docker run -p 8080:8080 -e BROKER_LIST="kafka:9092" -d pegerto/kinesthesia:latest
```

Once it runs: [ui](http://localhost:8080)

![Screenshot 2020-10-20 at 00 04 36](https://user-images.githubusercontent.com/261659/96520859-e3efb100-1267-11eb-9b3a-21ad368f8752.png)
