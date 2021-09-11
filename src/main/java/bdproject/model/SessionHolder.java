package bdproject.model;

import java.util.Optional;

public class SessionHolder {

    private Session session;

    private SessionHolder() {}

    private SessionHolder(final int id, final boolean isOperator, final String username) {
        session = new SessionImpl(id, isOperator, username);
    }

    public static SessionHolder empty() {
        return new SessionHolder();
    }

    public static SessionHolder of(final int id, final String username, final boolean isOperator) {
        return new SessionHolder(id, isOperator, username);
    }

    public Optional<Session> session() {
        return Optional.ofNullable(session);
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
        public int userId() {
            return id;
        }

        @Override
        public boolean isOperator() {
            return isOperator;
        }

        @Override
        public String username() {
            return username;
        }
    }
}
