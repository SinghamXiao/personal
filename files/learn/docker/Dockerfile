FROM ubuntu:latest

LABEL docker_user="singham xiao" \
      version=1.0 \
      mail=docker_user@mail.com

ENV PATH /usr/local/bin:$PATH

RUN ["apt-get", "update"]

WORKDIR /home/test

RUN ["ls", "-al"]

ADD command.md /home/test

COPY test.tar.xz /home/test

ADD test.tar.xz /home/test

ADD dockerTest .

RUN ["ls", "-al"]

CMD ["test"]

ENTRYPOINT ["./dockerTest"]