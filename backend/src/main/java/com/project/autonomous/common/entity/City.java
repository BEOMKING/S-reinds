package com.project.autonomous.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum City {
    서울특별시("서울특별시"),
    인천광역시("인천광역시"),
    고양시("고양시"),
    과천시("과천시"),
    광명시("광명시"),
    광주시("광주시"),
    구리시("구리시"),
    군포시("군포시"),
    김포시("김포시"),
    남양주시("남양주시"),
    동두천시("동두천시"),
    부천시("부천시"),
    성남시("성남시"),
    수원시("수원시"),
    시흥시("시흥시"),
    안산시("안산시"),
    안성시("안성시"),
    안양시("안양시"),
    양주시("양주시"),
    여주시("여주시"),
    오산시("오산시"),
    용인시("용인시"),
    의왕시("의왕시"),
    의정부시("의정부시"),
    이천시("이천시"),
    파주시("파주시"),
    평택시("평택시"),
    포천시("포천시"),
    하남시("하남시"),
    화성시("화성시"),
    가평군("가평군"),
    양평군("양평군"),
    연천군("연천군"),
    대전광역시("대전광역시"),
    세종특별자치시("세종특별자치시"),
    계룡시("계룡시"),
    공주시("공주시"),
    논산시("논산시"),
    당진시("당진시"),
    보령시("보령시"),
    서산시("서산시"),
    아산시("아산시"),
    천안시("천안시"),
    제천시("제천시"),
    청주시("청주시"),
    충주시("충주시"),
    금산군("금산군"),
    부여군("부여군"),
    서천군("서천군"),
    예산군("예산군"),
    청양군("청양군"),
    태안군("태안군"),
    홍성군("홍성군"),
    괴산군("괴산군"),
    단양군("단양군"),
    보은군("보은군"),
    영동군("영동군"),
    옥천군("옥천군"),
    음성군("음성군"),
    중평군("중평군"),
    진천군("진천군"),
    대구광역시("대구광역시"),
    경산시("경산시"),
    경주시("경주시"),
    구미시("구미시"),
    김천시("김천시"),
    문경시("문경시"),
    상주시("상주시"),
    안동시("안동시"),
    영주시("영주시"),
    영천시("영천시"),
    포항시("포항시"),
    고령군("고령군"),
    군위군("군위군"),
    봉화군("봉화군"),
    성주군("성주군"),
    영덕군("영덕군"),
    영양군("영양군"),
    예천군("예천군"),
    울릉군("울릉군"),
    울진군("울진군"),
    의성군("의성군"),
    청도군("청도군"),
    청송군("청송군"),
    칠곡군("칠곡군"),
    강릉시("강릉시"),
    동해시("동해시"),
    삼척시("삼척시"),
    속초시("속초시"),
    원주시("원주시"),
    춘천시("춘천시"),
    태백시("태백시"),
    고성군("고성군"),
    양구군("양구군"),
    양양군("양양군"),
    영월군("영월군"),
    인제군("인제군"),
    정선군("정선군"),
    철원군("철원군"),
    평창군("평창군"),
    부산광역시("부산광역시"),
    울산관역시("울산광역시"),
    거제시("거제시"),
    김해시("김해시"),
    밀양시("밀양시"),
    사천시("사천시"),
    양산시("양산시"),
    진주시("진주시"),
    창원시("창원시"),
    통영시("통영시"),
    거창군("거창군"),
//    고성군("고성군"),
    남해군("남해군"),
    산청군("산청군"),
    의령군("의령군"),
    창녕군("창녕군"),
    하동군("하동군"),
    함안군("함안군"),
    함양군("함양군"),
    합천군("합천군"),
    제주시("제주시"),
    서귀포시("서귀포시"),
    광주광역시("광주광역시"),
    광양시("광양시"),
    나주시("나주시"),
    목포시("목포시"),
    순천시("순천시"),
    여수시("여수시"),
    강진군("강진군"),
    고흥군("고흥군"),
    곡성군("곡성군"),
    구례군("구례군"),
//    고흥군("고흥군"),
    보성군("보성군"),
    화순군("화순군"),
    장흥군("장흥군"),
//    강진군("강진군"),
    해남군("해남군"),
    영암군("영암군"),
    무안군("무안군"),
    함평군("함평군"),
    영광군("영광군"),
    장성군("장성군"),
    완도군("완도군"),
    진도군("진도군"),
    신안군("신안군"),
    전주시("전주시"),
    군산시("군산시"),
    익산시("익산시"),
    남원시("남원시"),
    정읍시("정읍시"),
    김제시("김제시"),
    완주군("완주군"),
    진안군("진안군"),
    무주군("무주군"),
    장수군("장수군"),
    임실군("임실군"),
    순창군("순창군"),
    고창군("고창군"),
    부안군("부안군")
    ;

    String value;
    City(String value) { this.value = value; }
    public String value() { return value; }

    @JsonCreator
    public static City from(String s) {
        return City.valueOf(s.toUpperCase());
    }
}
