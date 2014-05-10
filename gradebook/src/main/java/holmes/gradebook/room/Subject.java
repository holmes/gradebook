package holmes.gradebook.room;

import java.util.List;

public class Subject {
  private final String name;
  private final List<Assignment> assignments;

  public Subject(String name, List<Assignment> assignments) {
    this.name = name;
    this.assignments = assignments;
  }
}
