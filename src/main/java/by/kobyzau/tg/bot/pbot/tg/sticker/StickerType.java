package by.kobyzau.tg.bot.pbot.tg.sticker;

import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public enum StickerType {
  COVID(
      "CAACAgIAAxkBAAIFe160P3RkZPrbeEviA4MPRJ-ayO0HAALtAQACVp29ChFLItZKWIpNGQQ",
      "CAACAgIAAxkBAAIFel60P2qjxpsx4b1-eA_GKAMp4KHkAAKoAAPkoM4HGX3fqOpZy1UZBA",
      "CAACAgIAAxkBAAIFeV60P2BM3b8eRjXeZH6CkvEcsnixAALIAQACVp29Ch5kbWu8BAS4GQQ",
      "CAACAgIAAxkBAAIFeF60P1xybXPOjQ-CljwuUnpFbT4iAALzAQACVp29CqoniIxoJUGPGQQ"),
  LOVE(
      "CAACAgIAAxkBAAICp15FNOPbCaclP-TfRBp8oSjPXlCAAAJsAAOWn4wOgCt-cSkIBlEYBA",
      "CAACAgIAAxkBAAICqF5FNXXx247rW2QUWxnL8uLNoEivAAIEAQACVp29Ct4E0XpmZvdsGAQ",
      "CAACAgIAAxkBAAICqV5FNX2v1MyMzRVrHVTxtF0LXwJ9AAITAAOvxlEaWm7uRWmjo3MYBA",
      "CAACAgIAAxkBAAICql5FNYzBOYDf36SuEK3ih1PeI7kUAAIjAAP3AsgPlczTTWG9fUcYBA"),
  LOL(
      "CAADAgADXgADUomRI_5xpNU-Z9mEFgQ",
      "CAADAgADmwMAAs-71A75gkZ93Rv-ixYE",
      "CAADAgADWAADUomRI32OzA4HOEuGFgQ",
      "CAADAgADIwADO2AkFGvbSvJGWstYFgQ",
      "CAADAgADYgADlp-MDgsNmImrEBX6FgQ",
      "CAADAgADAQADwDZPExguczCrPy1RFgQ"),
  COOL(
      "CAACAgIAAxkBAAIDGl5dWaspavqraICMt9i_vB-LDjtfAAJEAAOyglUgIu1BSIIMRJAYBA",
      "CAACAgIAAxkBAAIDGV5dWZ0IxU4kk5G2sfw40xfO9gwRAAInAAOyglUgDBYt5z5c5wkYBA",
      "CAACAgIAAxkBAAIDGF5dWZrZ5mnRySHxKXOViFgUS2KaAAI0AAOyglUgxWfTP59qRvEYBA",
      "CAADAgADCwADO2AkFMPPOZh4z_kHFgQ",
      "CAADAgADrQkAAnlc4gnGTO4AAVwKVRoWBA",
      "CAADAgAD6AIAArVx2gZSDa62VYCCWxYE",
      "CAADAgADJQADDbbSGYR9-DFmyxk_FgQ",
      "CAADAgADRgADUomRI_j-5eQK1QodFgQ",
      "CAADAgADBAADO2AkFLOr61RvleGrFgQ",
      "CAADAgADIAADlp-MDqz9QTP0qm_5FgQ",
      "CAADAgADowADMNSdEWmtlTFeBowaFgQ"),
  PIDOR(
      "CAACAgIAAxkBAAIDG15dYJKeTQ7B_cBQpDhzMhymtDKmAAKXAAPkoM4H7oUgYJJCasoYBA",
      "CAACAgIAAxkBAAIDHF5dYJNkBAIdn91wpz6tjyHaiMsgAAKfAAPkoM4Hcgov_iX58osYBA",
      "CAADAgADIgoAAlOx9wOADyaiQm0-qhYE",
      "CAACAgIAAxkBAAIGf19WWCfq_leSOgmEwH1HpPlm6EOeAAKPdAEAAWOLRgwqe9qGci9BrBsE",
      "CAADAgADaQMAAqdw6ghUIfn07qi3lhYE",
      "CAACAgIAAxkBAAIIgl9bOmXiaWUvFsdrPAvC2aiEowS8AAIXAAMq7r0SWFc0o2QsLcwbBA",
      "CAADAgADmwUAAlOx9wNCvw--ehyldxYE",
      "CAACAgIAAxkBAAIGgF9WWDVq4V9m3YqUKJcdZlCVudUZAAIcAAORaAwAAY0VMcNXmyMJGwQ",
      "CAADAgADnwUAAlOx9wOcWUVVk-EOiRYE",
      "CAADAgADoQUAAlOx9wMCmfXNSE716xYE",
      "CAADAgADpQUAAlOx9wOvbMIJtsAWphYE",
      "CAADAgADyT0AAlOx9wM-BQmopQzCdxYE",
      "CAACAgIAAxkBAAIIg19bOmVttE5NNRrcXnqfE-wI8Q1BAAIfAAMq7r0STr0soSPG8_4bBA",
      "CAACAgIAAxkBAAIIhF9bOmU96l2M_ximOZ7mwxS7josMAAIiAAMq7r0SP_NwCWjyVUIbBA",
      "CAACAgIAAxkBAAIIhV9bOmX17JZKPEWIpsw_2xs_-GugAAIqAAMq7r0SrzpV8bT3ZXMbBA",
      "CAACAgIAAxkBAAIIhl9bOmVYWP3pjjiprbVuvM746uN8AAIrAAMq7r0SX4YY4ODKMtgbBA",
      "CAADAgAD2nUBAAFji0YMUciy5pFv82EWBA"),
  LOOKING_PIDOR(
      "CAACAgIAAxkBAAIIh19bOvHnZOBHTKOXJ4BqKjPHEzFZAAIYAAMq7r0SNv28PCtEzgEbBA",
      "CAACAgIAAxkBAAIIiF9bOvOGUvKmWdLzS111zes2NZ3hAAIaAAMq7r0SDqKF5ZduBPEbBA",
      "CAACAgIAAxkBAAIIil9bOvXMGNtjVMiwN6xTQqD4o4-EAAIcAAMq7r0Sr6Sv9vZ8vZIbBA",
      "CAACAgIAAxkBAAIIi19bOvru29gpZO6o59H_Qb1fwjLJAAIgAAMq7r0SBH2IWBmxUJUbBA",
      "CAACAgIAAxkBAAIIjF9bOvtzsrAQb3A1qRXc7Ogqu8OEAAIhAAMq7r0SoVvQWOUTwU4bBA",
      "CAACAgIAAxkBAAIIjV9bOwL3UQX7695D4FNfq-TnhkYcAAIlAAMq7r0S89aZX54COhMbBA",
      "CAACAgIAAxkBAAIIjl9bOwQ2JaK-MQNlnOkfsXhrK3SmAAInAAMq7r0SkF78m_BqSI0bBA",
      "CAACAgIAAxkBAAIIj19bOwp2jLWwmr8AAY9adGRdCVqBaQACKQADKu69En2y-P08aKt0GwQ",
      "CAACAgIAAxkBAAIIkF9bOw2r-4EESQzM8xcn7nbjeVo8AAItAAMq7r0SzxS5zLu6prgbBA",
      "CAACAgIAAxkBAAIIkV9bOxUt39fID2XOjc0yTY2gS9LwAAI2AAMq7r0S5lpmxnXT3ZkbBA",
      "CAACAgIAAxkBAAIIiV9bOvUKdRL1QS3bRwAB-PVbeDZ_3wACGwADKu69EmZ3srn2eajxGwQ"),
  SAD(
      "CAADAgAD8wADVp29Cmob68TH-pb-FgQ",
      "CAADAgADJQAD9wLID_wNw-ch8MYWFgQ",
      "CAADAgADBwADr8ZRGrjX-OfHvPc0FgQ",
      "CAADAgADqQADMNSdEWpgUZQ54UcyFgQ",
      "CAADAgADogADFkJrCuweM-Hw5ackFgQ"),
  NEW_YEAR(
      "CAACAgIAAxkBAAJzjl_ptt5b7qO1qqvCLrCfy53tuY7AAAJLAAOYv4ANOPU9prV3wakeBA",
      "CAACAgIAAxkBAAJzj1_ptuS18aJZHhMs4xKz-di_AbCWAAI-AAOYv4ANplKfn-eWHsUeBA",
      "CAACAgIAAxkBAAJzkF_ptun3-0qlI3N_9d_uvkYMB7qRAAI_AAOYv4AN3HHlHcY-kU4eBA",
      "CAACAgIAAxkBAAJzkV_ptyjILwIcwxlO3AgdC6yxgQtLAAIaAAMoD2oUjGCfiGcEcm4eBA",
      "CAACAgIAAxkBAAJzkl_pty53spp-2K9x3e3BzC3Ds9cjAAIdAAMoD2oU9tZCerCdnFMeBA",
      "CAACAgIAAxkBAAJzk1_ptzI0jTU4HBasabLXDvcFSYgXAAJqBQACYyviCc0F9OjFsaerHgQ",
      "CAACAgIAAxkBAAJzlF_ptzk2X-T9XuibDcfVervM86UIAAJKBQACYyviCdlZk7AaIGlaHgQ",
      "CAACAgIAAxkBAAJzlV_pt0GqD0y7Mg2FtCCmGicL1GeLAAJaBQACYyviCaX5YX6iXAgfHgQ",
      "CAACAgIAAxkBAAJzll_pt0qXECriBnoQvWuClMwrwAjbAAKXBQACYyviCQv1Qp2kU5KbHgQ",
      "CAACAgIAAxkBAAJzl1_pt1CX8nN1XTO9UO3LPre2eUM0AAJSBQACYyviCZhfXltZ-9fDHgQ",
      "CAACAgIAAxkBAAJzmF_pt1avgsS6iNV7YofZYgGilwr-AAKhBQACYyviCb-y2FVgsZJ7HgQ",
      "CAACAgIAAxkBAAJzmV_pt1zSlyolV8AkQHIK4VTjei6oAAJ4BQACYyviCWzWEsEJOuIVHgQ",
      "CAACAgIAAxkBAAJzml_pt2Knr_8_-8_ZWFjabwy9QyroAAKKBQACYyviCUCOYJTmEybeHgQ",
      "CAACAgIAAxkBAAJzm1_pt2znNVMRMvGJlieuHmkOttzXAAKEBQACYyviCUf5zQ1Z74RjHgQ",
      "CAACAgIAAxkBAAJznF_pt3npYVvKaksgX6vVtbSzn7XvAAJNAQACJQNSD8u5WWesgP6HHgQ",
      "CAACAgIAAxkBAAJznV_pt3z5yPP_vUQEwn_5lG1DtxNhAAJOAQACJQNSDwfBzgpCwkZdHgQ",
      "CAACAgIAAxkBAAJznl_pt379zrkMn4HGb91dFZsd_WhKAAJPAQACJQNSD4i87fMDPWTxHgQ",
      "CAACAgIAAxkBAAJzn1_pt4Suvg1ewuTrjXjHXMl6l_4rAAJTAQACJQNSD96UlgABpAV3dR4E",
      "CAACAgIAAxkBAAJzoF_pt4kO2PCBPhG9J8RyT_nWEuepAAJaAQACJQNSD3msmJ3Ue-xwHgQ",
      "CAACAgIAAxkBAAJzoV_pt5EacEw0dnv97TVta-6XdnblAAJhAQACJQNSD9yym7INemY6HgQ"),
  CONGRATULATION(
      "CAACAgIAAxkBAAJyrV_cqc_nt8n5LCqZgXwCC62MK393AAJKAgACVp29CslqxmhgGHrwHgQ",
      "CAACAgIAAxkBAAJyrl_cqfSVu6Sh67DBYIjZZgPkRWwcAAIrAAMq7r0SX4YY4ODKMtgeBA",
      "CAACAgIAAxkBAAJyr1_cqkx9E6hX5Xzydi3KGCbUFyvWAAKEAgACXCiGCG0C3ss18mQIHgQ",
      "CAACAgIAAxkBAAJysV_cqnRBUY1jvfaZ3cI5zVEk4reUAAJAAANSiZEjNVy6P3rWQokeBA",
      "CAACAgIAAxkBAAJysl_cqoSyKm1y5PnQ_6QH_cKZLE-eAAI1AAM7YCQU6LQltBBmIyEeBA",
      "CAACAgIAAxkBAAJys1_cqo2S-U_z7OYMP7nTJMYiwHAPAAI3AAOWn4wONcYCrt_7dkkeBA",
      "CAACAgIAAxkBAAJytF_cqpbg9WpZvRpW6eO22LUiMfMUAAJKAgACVp29CslqxmhgGHrwHgQ",
      "CAACAgIAAxkBAAJytV_cqqRvBj7YW2v2eBmxVeTqyHThAALsAgACtXHaBvbV2g1oKhUwHgQ",
      "CAACAgIAAxkBAAJytl_cqq4_Vs22zT-i5UK0ZoEWekpQAAIaAAM7YCQUhjprJ-6zjmUeBA",
      "CAACAgIAAxkBAAJyt1_cqxjk9AWfVvclvFXESMJoHwABGgACowAD5KDOB1ixMqsvCWJJHgQ",
      "CAACAgIAAxkBAAJyuF_cqyhsSDzCigbhiN5DI7j7_h85AAKFAAPBnGAMi4wdH0hTXSIeBA",
      "CAACAgIAAxkBAAJyuV_cqzaibAWOc4V7flfnSxUPCm2FAAInAAP3AsgPS9klerRucBseBA",
      "CAACAgIAAxkBAAJyul_cqzhPLHrxDU8zn-G_GzSVu3LnAAIsAAP3AsgPvEHgyPvhQNMeBA",
      "CAACAgIAAxkBAAJyu1_cqz460etWW-hcLO0ge4RMq5FrAAIoAANZu_wl-CwR66M5TkseBA",
      "CAACAgIAAxkBAAJysF_cqmunN-UnZWfVt701bs3r1zkqAAKVAAPkoM4HokcHCBh97jIeBA"),
  // CUSTOM SET
  PIDOR_NIKITA(
      "CAADAgADBQADp93mGonETsrNE4M4FgQ",
      "CAADAgADEgADp93mGon5848AAcyTLhYE",
      "CAADAgADDQADp93mGlNI0S4T_0GhFgQ",
      "CAACAgIAAxkBAAJ1OGAJTDPNVJ0m6zqfR6byeb5osMt8AAIvAAOn3eYaeaYXgsKzux0eBA",
      "CAADAgADFAADp93mGh3XvflzFuCAFgQ",
      "CAADAgADDwADp93mGibAn5zHhuALFgQ"),
  PIDOR_VOVA(
      "CAADAgADGAADp93mGp_EsqroNzAEFgQ",
      "CAADAgADHQADp93mGic67L2qXP_4FgQ",
      "CAADAgADEAADp93mGs7SGfpzkfi1FgQ",
      "CAADAgADDAADp93mGvIp7FRyqZUYFgQ"),
  PIDOR_FIL(
      "CAACAgIAAxkBAAIDK15damHE45FauYyEWrHQmDe9GXDPAAIqAAOn3eYaA3lSbxduG6EYBA",
      "CAADAgADHwADp93mGtLxP6hBlPdWFgQ",
      "CAACAgIAAxkBAAJ1O2AJTDR6IrMaBwNaROAAAdnmNwydWQACLgADp93mGn7WpqUfrkG6HgQ",
      "CAADAgADGQADp93mGlsd0PiAymYVFgQ",
      "CAACAgIAAxkBAAIGfl9WV-shrHU9uUe-03SjrTrNaKMoAAIrAAOn3eYa7ysIXg65KMAbBA",
      "CAADAgADFwADp93mGhAG4L2ch2I_FgQ"),
  PIDOR_SANYA("CAADAgADAwADp93mGhKYjMXwcFNVFgQ", "CAADAgADEwADp93mGilg--ljJ5LFFgQ"),
  PIDOR_ARTYR(
      "CAADAgADIAADp93mGhEsb_aYoD2BFgQ",
      "CAADAgADBAADp93mGhOSvNb5xVyEFgQ",
      "CAADAgADCgADp93mGhRO6QbIvW_zFgQ"),
  PIDOR_MAX(
      "CAADAgADCAADp93mGkkyFqHiRk8PFgQ",
      "CAADAgADEQADp93mGtHW4fWzs3F7FgQ",
      "CAADAgADCQADp93mGvVD_YWe0JX8FgQ"),
  PIDOR_SHOHAN(
      "CAADAgADDgADp93mGip7wUAAARh-xxYE",
      "CAADAgADFQADp93mGurrGAJjwOMFFgQ",
      "CAACAgIAAxkBAAJ1OmAJTDTkdTyItUXVcK6oSwyanZV-AAItAAOn3eYawr8r-YHAzdkeBA",
      "CAADAgADGgADp93mGiz4dfL9tXrkFgQ"),
  PIDOR_DIMKA("CAACAgIAAxkBAAIDKl5damDFOwSx9CN9xl_ejqDkUn4ZAAIpAAOn3eYaDF5f97J_N-sYBA"),
  PIDOR_SERGEY_JOB(
      "CAACAgIAAxkBAAIDJl5dYtdRPjiK-J5YbcPazaHJeByNAAInAAOn3eYaT7nK4IkocmoYBA",
      "CAACAgIAAxkBAAIJOF4sdctVLCNXiHc85nPjGj1xvU6XAAImAAOn3eYa-r5Pl9UMrB4YBA",
      "CAACAgIAAxkBAAJ1OWAJTDQcdHSXZTsuhIqeslNsREQUAAIsAAOn3eYaBEJI4tVP9ogeBA",
      "CAACAgIAAxkBAAIJOV4sdcy_B1CoQLwE9JiMD_XL0GHWAAIlAAOn3eYaafcaVNvCSqIYBA",
      "CAACAgIAAxkBAAIJOl4sdczmvJo86BXrjid-bTJWqJ6GAAIkAAOn3eYavxlmLUdHgtEYBA");

  private final List<String> stickers;

  StickerType(String... stickers) {
    this.stickers = Arrays.asList(stickers);
  }

  public List<String> getStickers() {
    return stickers;
  }

  public String getRandom() {
    return CollectionUtil.getRandomValue(getStickers());
  }

  private boolean isPidor() {
    return name().startsWith("PIDOR_");
  }

  public static Optional<StickerType> parseSticker(String name) {
    final String stickerName = StringUtil.trim(name).toUpperCase();
    return EnumSet.allOf(StickerType.class).stream()
        .filter(s -> s.name().equals(stickerName))
        .findFirst();
  }

  public static Optional<StickerType> getPidorSticker(String name) {
    return parseSticker(name).filter(StickerType::isPidor);
  }
}
