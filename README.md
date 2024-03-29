## Мотивация:

По умолчанию утилита autossh не восстанавливает туннель после перезапуска системы или разрыва соединения. Её, конечно, можно запустить с помощью systemd, однако такой вариант, в сравнении с Docker, мне не подошел (хотя и в случае с Docker есть свои нюансы). В общем, в качестве альтернативы, решил использовать autossh в контейнере. К тому же, те решения, которые есть на текущий момент на Github, лично меня не устраивают, из-за чего озаботился созданием собственного образа.

## Предустановка:

Чтобы контейнер был успешно запущен, необходимо выполнить ряд предварительных требований:

- Для использования необходимо, чтобы на вашей системе был установлен Docker и Docker-compose.

- Для использования необходимо сгенерировать пару ключей.

- Для использования необходимо указать свои данные в параметре ENTRYPOINT, если мы говорим об иммутабельной сборке нашего образа:

## Запуск иммутабельного контейнера:

После того как проект был склонирован, необходимо перейти в директорию, чтобы собрать образ. Только перед этим стоит изменить содержимое строки entrypoint, подставив свои значения. В целом сделать это можно с помощью следующей команды:

```
docker build -t "autossh:autossh" -f ./immutable/autossh.dockerfile --build-arg SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)" .
```

где:

- autossh:autossh: это имя образа:тэга.
- autossh.dockerfile: путь к файлу Dockerfile.
- build-arg: аргумент, в который мы передаём приватный ключ, который в свою очередь располагается на хостовой системе. При этом публичный ключ уже должен быть расположен на целевом узле.

После того как образ был собран, контейнер можно запустить с помощью следующей команды:

```
docker run -d --name autossh --network host --restart unless-stopped autossh:autossh
```

## Запуск контейнера с переменными:

Если вам нужно запустить несколько контейнеров с разными значениями, можно использовать параметр -e. Только предварительно необходимо собрать образ на базе другого Dockerfile, который будет заточен под работу с переменными, так как содержимое несколько отлично от иммутабельного контейнера. Собрать образ можно с помощью следующей команды:

```
docker build -t "autossh-envs:autossh-envs" -f autossh.dockerfile --build-arg SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)" . 
```

Запуск контейнера будет выглядеть следующим образом:

```
docker run -d -e SSH_TUNNEL_PORT="22" -e SSH_TUNNEL_MODE="-L" -e SSH_TUNNEL_REMOTEPORT="80" -e SSH_TUNNEL_IP="192.168.88.218" -e SSH_TUNNEL_LOCALPORT="8080" -e SSH_TUNNEL_USER="vault" -e SSH_TUNNEL_HOST="192.168.88.218" --name autossh --network host --restart unless-stopped autossh-envs:autossh-envs
```

– где все переменные ссылаются на имена, указанные в скрипт файле autossh.sh, который после вызывается в Dockerfile с помощью entrypoint.

## Запуск через docker-compose:

По сути своей это та же сборка образа с указанием переменных, где запуск осуществляется не через команду docker run. Полезно будет, если вам нужно autossh добавить к уже существующему композу, где присутствуют прочие сервисы. Однако, чтобы передать ключ с хостового узла в аргумент, необходимо задействовать команду из вне, поэтому перед запуском docker-compose необходимо экспортировать ключ в переменную, поскольку реализацию запрашиваемой функции композ осуществить не может. Последовательность команд выглядит следующим образом:

```
export SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)"
```

После можно запускать docker-compose. Сделать это можно следующим образом:

```
docker-compose -f autossh-docker-compose.yaml up -d
```

Альтернативный вариант запуска заключен в файле Makefile, который делает тоже самое, но одной командой. Чтобы запустить, выполните:

```
make -f Makefile
```

Примечание: Перечисленные в композе переменные, по желанию, можно также записать в .env файл, предварительно создав его. А в самом композе, вместо существующих переменных, вызывать этот .env файл. Оба варианта одинаково применимы.

## Работа с autossh:

В данном случае команда выражена следующим образом:

```
autossh -N -M 0 -o "ServerAliveInterval 45" -o "ServerAliveCountMax 2" -o "StrictHostKeyChecking no" -i /home/autossh/.ssh/id_rsa -p 22 -L 8080:192.168.88.225:80 medoed@192.168.88.225
```

где:

- -N – отменяет попадание в консоль целевого сервера. По сути данный параметр позволяет указывать autossh в качестве точки входа.
- -M 0 – отключает мониторинг порта.
- -о "ServerAliveInterval 45" – количество секунд, которое клиент будет ожидать перед отправкой нулевого пакета на сервер.
- -о "ServerAliveCountMax 2" – будет пытаться выполнить описанный выше процесс до двух раз.
- -o "StrictHostKeyChecking no" – автоматическое принятие ключа. Данный параметр обязателен для работы контейнера.
- -i – указывается путь к приватному ключу внутри контейнера.
- -p – указывается номер порта удаленного сервера.
- -L 8080:192.168.88.225:80 – конструкция создаваемого туннеля.
- [medoed@192.168.88.225](https://www.twitch.tv/exitfound) – имя пользователя и IP-адрес целевого сервера.

В общем случае необходимо будет только подставить значение своего порта для SSH, тип туннеля и соответствующие порты для туннеля, а также имя пользователя и IP-адрес целевого сервера.

Более подробно про SSH-туннели можно узнать по следующей ссылке – https://t.me/opengrad/50.
