package flexmud;

public abstract class PersistedComparable {

    public abstract long getId();

    @Override
    public boolean equals(Object o) {
        return o != null && (super.equals(o) || isClassAndIdEqual(o));
    }

    private boolean isClassAndIdEqual(Object o) {
        return this.getClass().equals(o.getClass()) && this.getId() == ((PersistedComparable) o).getId();
    }
}
