package bdproject.model;

import java.util.Optional;

public class SessionHolder {

    private static Session session = null;

    private SessionHolder() {}

    public static void create(final int id, final boolean isOperator, final String username) {
        if (session != null) {
            throw new IllegalStateException("A session is already active!");
        } else {
            session = new SessionImpl(id, isOperator, username);
        }
    }

    public static Optional<Session> getSession() {
        return Optional.ofNullable(session);
    }

    public static void disconnect() {
        session = null;
    }

    /**
     *
     */
    private static class SessionImpl implements Session {

        private final int id;
        private final boolean isOperator;
        private final String username;

        public SessionImpl(final int id, final boolean isOperator, final String username) {
            this.id = id;
            this.isOperator = isOperator;
            this.username = username;
        }

        @Override
        public int getUserId() {
            return id;
        }

        @Override
        public boolean isOperator() {
            return isOperator;
        }

        @Override
        public String getName() {
            return username;
        }
    }
}
