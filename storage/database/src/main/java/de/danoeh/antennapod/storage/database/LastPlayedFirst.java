package de.danoeh.antennapod.storage.database;

import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;

import java.util.List;

public final class LastPlayedFirst {
    private LastPlayedFirst() {
    }

    public static void apply(List<FeedItem> queue) {
        int lastPlayedIndex = -1;
        long lastPlayedTime = 0;
        for (int i = 0; i < queue.size(); i++) {
            FeedItem item = queue.get(i);
            FeedMedia media = item.getMedia();
            if (media == null || item.isPlayed() || media.getPosition() <= 0) {
                continue;
            }
            if (media.getLastPlayedTimeStatistics() > lastPlayedTime) {
                lastPlayedTime = media.getLastPlayedTimeStatistics();
                lastPlayedIndex = i;
            }
        }
        if (lastPlayedIndex > 0) {
            queue.add(0, queue.remove(lastPlayedIndex));
        }
    }
}
