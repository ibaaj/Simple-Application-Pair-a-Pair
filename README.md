Simple-Application-Pair-a-Pair
==============================


# Sujet

On veut créer une application client/serveur et pair à pair. Un ensemble de clients participent à une application. Chaque client posséde initialement une donnée (par exemple une chaîne de caractères).
Chaque client veut obtenir 5 données. Pour se faire, il veut savoir s’il y a d’autres clients connectés qui possédent des données et à quelle adresse commu- niquer avec eux pour les obtenir.
Un serveur centralise les informations, le site lequel s’exécute le serveur et le port sur lequel il attend sont supposés connus par les clients.
– Le serveur attend une requête du client.
– Le client envoie au serveur le site et le port sur lequel il attendra des
communications des autres clients.
– Le serveur envoie alors au client la liste des clients connectés.
– Le client demande une donnée aux autres clients et attend de recevoir la
donnée des clients qu’il a contactés.
– Quand un client a re cu 5 données et ne veut plus participer il prévient le
serveur.
Le serveur et les clients communiquent via des sockets TCP. Entre eux les clients communiquent via des sockets UDP.

# Réalisation 

Lire le rapport à l'aide du pdf joint.

==============================

MIT License
