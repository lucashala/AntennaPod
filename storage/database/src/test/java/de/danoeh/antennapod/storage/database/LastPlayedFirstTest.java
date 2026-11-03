package de.danoeh.antennapod.storage.database;

import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LastPlayedFirstTest {
    private static FeedItem episode(long id, int position, long lastPlayed, boolean played) {
        FeedItem item = new FeedItem();
        item.setId(id);
        FeedMedia media = new FeedMedia(item, "https://example.com/" + id, 1, "audio/mpeg");
        media.setPosition(position);
        media.setLastPlayedTimeStatistics(lastPlayed);
        item.setMedia(media);
        item.setPlayed(played);
        return item;
    }

    private static List<Long> ids(List<FeedItem> queue) {
        List<Long> result = new ArrayList<>();
        for (FeedItem item : queue) {
            result.add(item.getId());
        }
        return result;
    }

    @Test
    public void movesMostRecentUnfinishedEpisodeToFrontAndKeepsOtherOrder() {
        List<FeedItem> queue = new ArrayList<>(Arrays.asList(
                episode(1, 0, 0, false),
                episode(2, 500, 100, false),
                episode(3, 0, 0, false),
                episode(4, 700, 200, false)));
        LastPlayedFirst.apply(queue);
        assertEquals(Arrays.asList(4L, 1L, 2L, 3L), ids(queue));
    }

    @Test
    public void movesReplayedEpisodeAndIgnoresEpisodeWithoutPosition() {
        List<FeedItem> queue = new ArrayList<>(Arrays.asList(
                episode(1, 0, 0, false),
                episode(2, 500, 100, false),
                episode(3, 900, 300, true),
                episode(4, 0, 400, true)));
        LastPlayedFirst.apply(queue);
        assertEquals(Arrays.asList(3L, 1L, 2L, 4L), ids(queue));
    }

    @Test
    public void keepsOrderWhenNothingIsInProgress() {
        List<FeedItem> queue = new ArrayList<>(Arrays.asList(
                episode(1, 0, 0, false),
                episode(2, 0, 500, false)));
        LastPlayedFirst.apply(queue);
        assertEquals(Arrays.asList(1L, 2L), ids(queue));
    }

    @Test
    public void handlesEmptyQueue() {
        List<FeedItem> queue = new ArrayList<>();
        LastPlayedFirst.apply(queue);
        assertEquals(0, queue.size());
    }
}
