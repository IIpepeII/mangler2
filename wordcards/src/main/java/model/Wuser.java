package model;

import controller.Controller;

import java.util.List;

/**
 * Wuser instances contains every necessary information
 * about the current user in session.
 */
public class Wuser {

    private int result = 0;
    private List<WordCard> cards;
    private int cardListSize;
    private int currentCardIndex = 0;
    private String startTime;
    private String endTime;

    public Wuser() {
    }

    public List<WordCard> getCards() {
        return cards;
    }

    public void setCards(List<WordCard> cards) {
        this.cards = cards;
        this.cardListSize = cards.size();
    }

    public WordCard getCard() {
        int currentIndex = currentCardIndex;
        currentCardIndex++;
        return cards.get(currentIndex);
    }

    public boolean evalCard(String hun, String eng) {
        WordCard card = cards.get(currentCardIndex - 1);
        return card.getHun().equals(hun) && card.getEng().equals(eng);
    }

    public boolean evalCardWithIndexZero(String hun, String eng) {
        WordCard card = cards.get(0);
        return card.getHun().equals(hun) && card.getEng().equals(eng);
    }

    public int getCardListSize() {
        return cardListSize;
    }

    public boolean isLastIndex() {
        return currentCardIndex == cardListSize;
    }

    public int getResult() {
        return result;
    }

    public void setResultToZero() {
        result = 0;
    }

    public void setCurrentCardIndexToZero() {
        currentCardIndex = 0;
    }

    public int getCurrentIndex() {
        return currentCardIndex;
    }

    public void setResult() {
        result++;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTime = Controller.getCurrentTime();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime() {
        this.endTime = Controller.getCurrentTime();
    }

}
