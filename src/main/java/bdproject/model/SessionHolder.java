package bdproject.model;

import java.util.Optional;

public class SessionHolder {

    private static Session session = null;

    private SessionHolder() {}

    public static void create(final int id, final byte admin, final String username) {
        if (session != null) {
            throw new IllegalStateException("A session is already active!");
        } else {
            session = new SessionImpl(id, admin, username);
        }
    }

    public static Optional<Session> get() {
        return Optional.ofNullable(session);
    }

    public static void disconnect() {
        session = null;
    }


    private static class SessionImpl implements Session {

        private final int id;
        private final byte admin;
        private final String username;

        public SessionImpl(final int id, final byte admin, final String username) {
            this.id = id;
            this.admin = admin;
            this.username = username;
        }

        @Override
        public int getUserId() {
            return id;
        }

        @Override
        public boolean isAdmin() {
            return admin == (byte) 1;
        }

        @Override
        public String getName() {
            return username;
        }
    }
}
