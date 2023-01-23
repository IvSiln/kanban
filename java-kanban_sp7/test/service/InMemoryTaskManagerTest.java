package service;

import org.junit.jupiter.api.BeforeEach;
import util.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
