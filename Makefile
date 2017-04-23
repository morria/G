all:
	mvn package
	docker build -t morria/go .

run:
	docker run -it -p 80:7070 -e VIRTUAL_HOST=g.local.collapse.io --rm morria/go

push:
	docker push morria/go
