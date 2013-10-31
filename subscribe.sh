#! /bin/sh


curl --request POST http://localhost:8080/resources/player/register?email=miguel.basire@gmail.com\&pseudo=Otis\&serverURL=http://localhost:8883
curl --request POST http://localhost:8080/resources/player/register?email=miguel.basire1@gmail.com\&pseudo=UpAndDown\&serverURL=http://localhost:8882
curl --request POST http://localhost:8080/resources/player/register?email=miguel.basire2@gmail.com\&pseudo=Omnibus\&serverURL=http://localhost:8881

