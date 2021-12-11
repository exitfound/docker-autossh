## Мотивация:

По умолчанию утилита autossh не восстанавливает туннель после перезапуска системы или разрыва соединения. Её, конечно, можно запустить через systemd, однако такой вариант, в сравнении с Docker, достаточно избыточен. Поэтому, в качестве альтернативы, решено было запускать autossh в контейнере. К тому же, те решения, которые есть на текущий момент на Github, меня лично не устраивают.

## Предустановка:

Чтобы контейнер был успешно запущен, необходимо выполнить ряд предварительных требований:

- Для использования необходимо, чтобы на вашей системе был установлен Docker и Docker-compose.

- Для использования необходимо сгенерировать пару ключей.

- Для использования необходимо указать свои данные в параметре ENTRYPOINT, если мы говорим об иммутабельной сборке образа:

## Запуск иммутабельного контейнера:

После того как проект был склонирован, необходимо перейти в диреткорию, чтобы собрать образ. Сделать это можно с помощью следующей команды:

`docker build -t "autossh:autossh" -f autossh.dockerfile --build-arg SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)" .`

где:

- autossh:autossh: это имя образа:тэга.
- autossh.dockerfile: путь к файлу Dockerfile.
- build-arg: аргумент, в который мы передаём приватный ключ, который в свою очередь располагается на хостовой системе. При этом публичный ключ уже должен быть расположен на целевом узле.

После того как образ был собран, контейнер можно запустить с помощью следующей команды:

`docker run -d --name autossh --network host --restart unless-stopped autossh:autossh`

## Запуск контейнера с переменными:

Если вам нужно запустить несколько контейнеров с разными значениями, можно использовать параметр -e. Только предварительно необходимо собрать образ на базе другого Dockerfile, который будет заточен под работу с переменными, так как содержимое несколько отлично от иммутабельного контейнера. Собрать образ можно с помощью следующей команды:

`docker build -t "autossh-envs:autossh-envs" -f autossh-with-envs.dockerfile --build-arg SSH_PRV_KEY="$(cat ~/.ssh/id_rsa)" . `

Запуск контейнера будет выглядеть следующим образом:

`docker run -d -e <YOUR_ENV> --name autossh --network host --restart unless-stopped autossh-envs:autossh-envs`

## Запуск через docker-compose:

По сути своей это та же сборка образа с указанием переменных, где запуск осуществляется не через команду docker run. Полезно будет, если вам нужно autossh добавить к уже существующему композу, где присутствуют прочие сервисы. Однако, чтобы передать ключ с хостового узла в аргумент, необходимо задействовать команду из вне, поэтому запуск осуществляется через сторонний скрипт, поскольку реализацию запрашиваемого функционала композ осуществить не смог. Чтобы запустить скрипт, необходимо назначить соответствующие права:

`chmod +x key-export.sh`

После можно запускать сам скрипт. Сделать это можно следующим образом:

`.key-export.sh`

## Работа с autossh:

В данном случае команда выражена следующим образом:

`autossh -N -M 0 -o "ServerAliveInterval 45" -o "ServerAliveCountMax 2" -o "StrictHostKeyChecking no" -i /home/autossh/.ssh/id_rsa -p 2000 -L 3000:localhost:3000 root@10.0.0.1`

где:

- -N – отменяет попадание в консоль целевого сервера. По сути данный параметр позволяет указывать autossh в качестве точки входа.
- -M 0 – отключает мониторинг порта.
- -о "ServerAliveInterval 45" – количество секунд, которое клиент будет ожидать перед отправкой нулевого пакета на сервер.
- -о "ServerAliveCountMax 2" – будет пытаться выполнить описанный выше процесс до двух раз.
- -o "StrictHostKeyChecking no" – автоматическое принятие ключа. Данный параметр обязателен для работы контейнера.
- -i – указывается путь к приватному ключу внутри контейнера.
- -p – указывается номер порта удаленного сервера.
- -L 3000:localhost:3000 – конструкция создаваемого туннеля.
- [root@10.0.0.1](https://www.twitch.tv/exitfound) – имя пользователя и IP-адрес целевого сервера.

В общем случае необходимо будет только подставить значение своего порта для SSH, тип туннеля и соответствующие порты для туннеля, а также имя пользователя и IP-адрес целевого сервера.

Более подробно про SSH-туннели можно узнать по следующей ссылке – https://t.me/opengrad/50.