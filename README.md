# G

G is a bookmark tool that stays out of your way. You can see it
running at https://collapse.io.

## Running G

To run the service, you can execute the following which will compile
the package (via Maven) and launch it on port 7070.

```sh
AWS_ACCESS_KEY_ID=<KEY_ID>; AWS_SECRET_KEY=<KEY>; ./bin/dev
```

Once its up and running, you can go to <a href="http://localhost:7070">http://localhost:7070</a>
in your browser.

## Docker

To create and run G as a docker container, run the following.

```sh
make all
docker run -it -p 80:7070 -e VIRTUAL_HOST=collapse.io -e AWS_ACCESS_KEY_ID=<KEY_ID> -e AWS_SECRET_KEY=<KEY> --rm morria/go
```
