package com.nazar.entityMenager;

public final class EntityManagerConfiguration {
    private final String entityPackage;
    private final String URL;
    private final String user;
    private final String password;

    private EntityManagerConfiguration(EntityManagerConfigurationBuilder builder) {
        this.entityPackage = builder.entityPackage;
        this.URL = builder.URL;
        this.user = builder.user;
        this.password = builder.password;
    }


    public String getEntityPackage() {
        return entityPackage;
    }

    public String getURL() {
        return URL;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public static class EntityManagerConfigurationBuilder {
        private String entityPackage;
        private String URL;
        private String user;
        private String password;

        public EntityManagerConfigurationBuilder setEntityPackage(String entityPackage) {
            this.entityPackage = entityPackage;
            return this;
        }

        public EntityManagerConfigurationBuilder setURL(String URL) {
            this.URL = URL;
            return this;
        }

        public EntityManagerConfigurationBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public EntityManagerConfigurationBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public EntityManagerConfiguration build() {
            return new EntityManagerConfiguration(this);
        }
    }
}
