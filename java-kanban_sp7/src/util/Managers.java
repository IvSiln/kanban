package util;

import service.HistoryManager;
import service.InMemoryHistoryManager;

public class Managers {
      public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
