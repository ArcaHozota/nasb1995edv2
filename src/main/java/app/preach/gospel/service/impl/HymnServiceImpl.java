package app.preach.gospel.service.impl;

import java.util.List;

import org.jdbi.v3.core.JdbiException;
import org.springframework.stereotype.Service;

import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.Pagination;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 賛美歌サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HymnServiceImpl implements IHymnService {

//	/**
//	 * 日時フォマーター
//	 */
//	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//
//	/**
//	 * ランドム選択
//	 */
//	private static final Random RANDOM = new Random();

	@Override
	public CoResult<Integer, JdbiException> checkDuplicated(final String id, final String nameJp) {
		return null;
	}

	@Override
	public CoResult<HymnDto, JdbiException> getHymnInfoById(final Long id) {
		return null;
	}

	@Override
	public CoResult<Pagination<HymnDto>, JdbiException> getHymnsByKeyword(final Integer pageNum, final String keyword) {
		return null;
	}

	@Override
	public CoResult<List<HymnDto>, JdbiException> getHymnsRandomFive(final String keyword) {
		return null;
	}

	@Override
	public CoResult<Long, JdbiException> getTotalRecords() {
		return null;
	}

	@Override
	public CoResult<String, JdbiException> infoDeletion(final Long id) {
		return null;
	}

	@Override
	public CoResult<Integer, JdbiException> infoStorage(final HymnDto hymnDto) {
		return null;
	}

	@Override
	public CoResult<String, JdbiException> infoUpdation(final HymnDto hymnDto) {
		return null;
	}

//	/**
//	 * 漢字から片仮名へ転換する
//	 *
//	 * @param inputText インプットストリング
//	 * @return 片仮名
//	 */
//	private String kanjiToKatakana(final String inputText) {
//		// レギューラーエクスプレスで漢字、平仮名及び片仮名を抽出する
//		final String regex = "[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]+";
//		final StringBuilder builder = new StringBuilder();
//		for (final char ch : inputText.toCharArray()) {
//			final String inputChar = String.valueOf(ch);
//			if (Pattern.matches(regex, inputChar)) {
//				builder.append(inputChar);
//			}
//		}
//		// トークンで漢字の振り仮名を取得する
//		final Tokenizer tokenizer = new Tokenizer();
//		final List<Token> tokens = tokenizer.tokenize(builder.toString());
//		final StringBuilder result = new StringBuilder();
//		for (final Token token : tokens) {
//			result.append(token.getReading());
//		}
//		return result.toString();
//	}

//	/**
//	 * ランドム選択ループ1
//	 *
//	 * @param hymnsRecords 選択したレコード
//	 * @param totalRecords 総合レコード
//	 * @return List<Hymn>
//	 */
//	private List<Hymn> randomFiveLoop(final List<Hymn> hymnsRecords, final List<Hymn> totalRecords) {
//		final List<Hymn> concernList1 = new ArrayList<>(hymnsRecords);
//		final List<Long> ids = hymnsRecords.stream().map(Hymn::getId).toList();
//		final List<Hymn> filteredRecords = totalRecords.stream().filter(item -> !ids.contains(item.getId())).toList();
//		if (hymnsRecords.size() < ProjectConstants.DEFAULT_PAGE_SIZE) {
//			final int sagaku = ProjectConstants.DEFAULT_PAGE_SIZE - hymnsRecords.size();
//			for (int i = 0; i < sagaku; i++) {
//				final int indexOf = RANDOM.nextInt(filteredRecords.size());
//				final Hymn hymnsRecord = filteredRecords.get(indexOf);
//				concernList1.add(hymnsRecord);
//			}
//		}
//		final List<Hymn> concernList2 = concernList1.stream().distinct().toList();
//		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
//			return concernList2;
//		}
//		return this.randomFiveLoop(concernList2, filteredRecords);
//	}

//	/**
//	 * ランドム選択ループ2
//	 *
//	 * @param hymnsRecords 選択したレコード
//	 * @return List<Hymn>
//	 */
//	private List<Hymn> randomFiveLoop2(final List<Hymn> hymnsRecords) {
//		final List<Hymn> concernList1 = new ArrayList<>();
//		for (int i = 0; i < ProjectConstants.DEFAULT_PAGE_SIZE; i++) {
//			final int indexOf = RANDOM.nextInt(hymnsRecords.size());
//			final Hymn hymnsRecord = hymnsRecords.get(indexOf);
//			concernList1.add(hymnsRecord);
//		}
//		final List<Hymn> concernList2 = concernList1.stream().distinct().toList();
//		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
//			return concernList2;
//		}
//		return this.randomFiveLoop(concernList2, hymnsRecords);
//	}

	@Override
	public CoResult<String, JdbiException> scoreStorage(final byte[] file, final Long id) {
		return null;
	}

//	/**
//	 * セリフの全半角スペースを削除する
//	 *
//	 * @param serif セリフ
//	 * @return トリムドのセリフ
//	 */
//	private String trimSerif(final String serif) {
//		final String zenkakuSpace = "\u3000";
//		final String replace = serif.replace(zenkakuSpace, CoProjectUtils.EMPTY_STRING);
//		final String hankakuSpace = "\u0020";
//		return replace.replace(hankakuSpace, CoProjectUtils.EMPTY_STRING);
//	}

}
