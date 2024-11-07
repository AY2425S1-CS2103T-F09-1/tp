package seedu.address.model.person;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

public class EventIdsContainsIdsPredicate implements Predicate<Person> {
    private final List<Integer> ids;

    public EventIdsContainsIdsPredicate(List<Integer> ids) {
        this.ids = ids;
    }

    @Override
    public boolean test(Person person) {
        Set<Integer> eventIds = person.getEventIds();
        return ids.stream().anyMatch(id -> eventIds.contains(id));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventIdsContainsIdsPredicate)) {
            return false;
        }

        EventIdsContainsIdsPredicate otherEventIdsContainsIdsPredicate =
                (EventIdsContainsIdsPredicate) other;
        return ids.equals(otherEventIdsContainsIdsPredicate.ids);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", ids).toString();
    }
}
