# CommentBottomSheet
Instagram을 참고해 만든 custom bottom sheet

## 위젯을 만든 이유

ModalBottomSheet로 간단히 구현하려 했는데,모달 상태에서 하단에 입력창을 고정하는 방법이 마땅치 않음.<br>
BottomSheetScaffold는 시트 위에 위젯 배치가 가능해서,더 유연하게 커스텀할 수 있어 이걸로 결정함.

## Feature

1. 하단 고정된 TextField
2. BottomSheet가 사라질 때 TextField도 함께 내려감
3. BottomSheetScaffold에 배경 dim 효과 적용
4. 스크롤 위치에 따라 dim 강도 동적 조절

### 1. 하단 고정된 TextField

고정된 input() 영역 구현은 간단.<br>
Box로 감싼 후 input영역을 box하단에 배치

FixedInputBottomSheetScaffold.kt
```
Box(modifier = modifier.imePadding()) { // edge to edge 에서 imePadding을 줘야 하단 영역이 적용됨
    TorangBottomSheetScaffold(
		...
    )
    if (show) // sheet가 보이면 input 영역 보이기
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .absoluteOffset(y = inputOffset),
        ) {
            input()
        }
}
```
<img src ="../screenshots/PreviewFixedInputBottomSheetScaffold.png" width="400">

### 2. BottomSheet가 사라질 때 TextField도 함께 내려감

a : bottom sheet 높이
b : drag hadler 높이
c : comment widget 높이

a < b + c -> c -  