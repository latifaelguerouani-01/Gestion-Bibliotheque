
SELECT
    @@hostname AS serveur,
    @@port AS port_mysql,
    CURRENT_USER() AS utilisateur,
    DATABASE() AS base_active;
