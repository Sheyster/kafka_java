Start first zookeeper server
./zookeeper-server-start.sh ../config/zookeeper.properties

After zookeeper start kafka server
./kafka-server-start.sh ../config/server.properties

JAVA_HOME home variable
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64


Create a topic:
./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVO_PEDIDO


Verify topic created
./bin/kafka-topics.sh --list --bootstrap-server localhost:9092

Verify comsumer
./bin/kafka-console-consumer.sh --bootstrap-server 172.24.113.53:9092 --topic ECOMMERCE_NEW_ORDER --from-beginning


Verify topics
./bin/kafka-topics.sh --bootstrap-server 172.28.10.205:9092 --describe


This command didn't work in kafka version 2.13
./bin/kafka-topics.sh --alter --zookeeper localhost:2181 --topic ECOMMERCE_NEW_ORDER --partitions 5

The follow command worded very well to update kafka partitions
./bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic ECOMMERCE_NEW_ORDER --partitions 5



Testei na versão 2.13 e não funcionou, tudo indica que funcionaria em versões mais antigas
./bin/kafka-topics.sh --alter --zookeeper localhost:2181 --topic NEW_ORDER --partitions 5

Este comando funcionou na versão 2.13
./bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic NEW_ORDER --partitions 5


CQRS

BRSPBRJ121KWPT.brsp.capco.com:9092


For us keep save our application we need create a new replication, so we need another kafka up in another machine,
for that, we need run the follow command



We can't alter replication of kafka after started, so we need to change our server.properties and then start it
default.replication.factor=2


For any environment, except development, must be used: (this number 3 was suggested by kafka documentation
offsets.topic.replication.factor=3
transaction.state.log.replication.factor=3