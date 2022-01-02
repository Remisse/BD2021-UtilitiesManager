package bdproject.model;

import java.util.Optional;

public class SessionHolder {

    private Session session;

    private SessionHolder() {
    }

    private SessionHolder(final int id, final boolean isEmployee, final String username) {
        session = new SessionImpl(id, isEmployee, username);
    }

    public static SessionHolder empty() {
        return new SessionHolder();
    }

    public static SessionHolder of(final int id, final String username, final boolean isEmployee) {
        return new SessionHolder(id, isEmployee, username);
    }

    public Optional<Session> session() {
        return Optional.ofNullable(session);
    }


    /**
     *
     */
    private static class SessionImpl implements Session {

        private final int id;
        private final boolean isEmployee;
        private final String username;

        public SessionImpl(final int id, final boolean isEmployee, final String username) {
            this.id = id;
            this.isEmployee = isEmployee;
            this.username = username;
        }

        @Override
        public int userId() {
            return id;
        }

        @Override
        public boolean isEmployee() {
            return isEmployee;
        }

        @Override
        public String username() {
            return username;
        }
    }
}
