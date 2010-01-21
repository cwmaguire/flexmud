package flexmud;

public abstract class PersistedComparable {

    public abstract long getId();

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (super.equals(o)) {
            return true;
        }

        return isClassAndIdEqual(o);
    }

    private boolean isClassAndIdEqual(Object o) {
        return this.getClass().equals(o.getClass()) && this.getId() == ((PersistedComparable) o).getId();
    }
}
