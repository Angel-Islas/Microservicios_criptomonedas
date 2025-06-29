# Microservicios Criptomonedas

Este proyecto implementa una arquitectura de microservicios en Java para el seguimiento y análisis de precios de criptomonedas, utilizando una base de datos PostgreSQL y Docker Compose para la orquestación de servicios.

## Servicios incluidos

- **db**: Base de datos PostgreSQL.
- **datacollector**: Servicio para recolectar y guardar datos de precios.
- **cryptopriceapi**: API para consultar precios de criptomonedas.
- **graphservice**: Servicio para graficar datos históricos.
- **regressionservice**: Servicio para análisis de regresión.
- **frontend**: Interfaz web simple (Nginx).

## Requisitos previos

- [Java](https://www.java.com/es/download/linux_manual.jsp)
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/)
> **Nota:** En sistemas Linux, puede que necesites permisos de root (`sudo`) para ejecutar los comandos de Docker.

## Uso con docker

Clona este repositorio y navega a la carpeta principal:

```sh
git clone <url-del-repo>
cd Microservicios_criptomonedas
```

### Levantar todos los servicios
```sh
docker compose up
```
Esto iniciará todos los servicios y mostrará los logs en la terminal.

### Levantar en segundo plano (`detached`)
```sh
docker compose up -d
```

### Detener los servicios
```sh
docker compose down
```
Esto detendra todos los servicios creados por docker y eliminara dichos servicios, incluyendo los `volumes`.

### Ver logs de un servicio específico
```sh
docker compose logs <nombre_servicio>
```
Por ejemplo, para ver los logs de la base de datos:
```sh
docker compose logs db
```

## Uso sin docker
Clona este repositorio y navega a la carpeta principal:

```sh
git clone <url-del-repo>
cd Microservicios_criptomonedas
```

### Levantar todos los servicios
```sh
./iniciar_todo_linux.sh
```
Esto iniciará todos los servicios en segundo plano y mostrará los logs en la terminal conforme vayan saliendo.

### Mostrar la vista principal
En este caso, al no tener un servicio dedicado, se debe ejecutar el `index.html` de manera manual, es decir, dando doble click al archivo o en una **terminal de powershell** colocando el comando `./index.html` para visualizarlo en el navegador y pueda interactuar con el sistema.

### Detener los servicios
```sh
./detener_todo_linux.sh
```
Esto detendra todos los servicios en segundo plano, matando los procesos previamente guardados en el archivo `cryptotracker.pids`

## Notas adicionales
- Si usas Linux y no tienes permisos para Docker, antepone sudo a los comandos, por ejemplo:
`sudo docker compose up`

- Los datos de la base de datos se almacenan en un volumen persistente (`pg_data`).

- Puedes modificar los puertos expuestos en el archivo `docker-compose.yml` según tus necesidades.