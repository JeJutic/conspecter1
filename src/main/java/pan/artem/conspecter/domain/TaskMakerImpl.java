package pan.artem.conspecter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TaskMakerImpl implements TaskMaker {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public TaskMakerImpl() {
//        String test = "\\begin{itemize}\n" +
//                "        \\item first\n" +
//                "        \\item second\n" +
//                "        \\item third\n" +
//                "        \\item fourth\n" +
//                "    \\end{itemize}";
//        logger.info("is rus: {}", Character.isAlphabetic('ф'));
//        logger.info("here it is: {}", findWords(test));
//        logger.info("here it is: {}", makeTasks(test, 2));
    }

    private record Word(int pos, String word) {
    }

    private record Change(int pos, int length, String newWord) {
    }

    private boolean isWordCharacter(char c) {
        return Character.isAlphabetic(c) || c == '-';
    }

    private List<Word> findWords(String text) {
        List<Word> ans = new ArrayList<>();
        StringBuilder curWord = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (isWordCharacter(c)) {
                if (curWord != null) {
                    curWord.append(c);
                }
            } else if (Character.isWhitespace(c)) {
                if (curWord != null && 4 <= curWord.length() && curWord.length() <= 15) {
                    ans.add(new Word(i - curWord.length(), curWord.toString()));
                }
                curWord = new StringBuilder();
            } else {
                curWord = null;
            }
        }
        return ans;
    }

    private String makeChanges(String original, List<Change> changes, List<Integer> toApply) {   // words are sorted by pos
        StringBuilder res = new StringBuilder();
        int last = 0;
        for (int pos : toApply) {
            final Change change = changes.get(pos);
            res.append(original, last, change.pos());
            res.append(change.newWord());
            last = change.pos() + change.length();
        }
        res.append(original.substring(last));
        return res.toString();
    }

    private List<Integer> generateApplySeq(int cnt, int coef) {
        while (true) {
            List<Integer> res = new ArrayList<>();
            for (int i = 0; i < cnt; i++) {
                if (new Random().nextInt(coef) == 0) {
                    res.add(i);
                }
            }
            if (res.size() >= 2) {
                return res;
            }
        }
    }

    @Override
    public List<Task> makeTasks(String text, int taskCnt) {
        logger.info("Making tasks for original text: {}", text);

        var words = findWords(text);
        if (words.size() < 3) {
            return List.of();
        }
        List<Change> changes = new ArrayList<>();
        for (var word : words) {
            changes.add(new Change(word.pos(), word.word().length(), "!skipped!"));
        }
        logger.info("change: {}", changes);
        List<Task> res = new ArrayList<>();
        for (int i = 0; i < taskCnt; i++) {
            var applySeq = generateApplySeq(words.size(), 3);
            StringBuilder answer = new StringBuilder();
            for (int pos : applySeq) {
                answer.append(words.get(pos).word()).append(' ');
            }
            String newText = makeChanges(text, changes, applySeq);
            res.add(new Task(newText, answer.toString()));
        }

        logger.info("Generated tasks: {}", res);
        return res;
    }
}
