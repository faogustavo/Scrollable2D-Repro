# Scrollable2D Sample - Bug Reproducer

This project is a reproducer for issues with the `scrollable2D` Compose modifier across different platforms.

## Issue Description

The 2D scrolling functionality works correctly on **Android/iOS (mobile platforms)**, but **fails to work on JVM (Desktop) and Web (JS/Wasm)**.

## Sample Implementation

The sample code is located in `composeApp/src/commonMain/kotlin/org/example/project/App.kt` and demonstrates:

### Key Components for Triage

1. **ScrollableContainer** (`App.kt:62-108`): A custom scrollable container using `scrollable2D` modifier
   - Uses `rememberScrollable2DState` to handle scroll deltas
   - Implements manual offset calculation and viewport management
   - Uses `Layout` composable with custom placement logic

2. **Scrollable2D Configuration** (`App.kt:75-92`):
   - The following implementation was based on [this](https://android-review.googlesource.com/c/platform/frameworks/support/+/3870799) commit from the AOSP.  
   ```kotlin
   .scrollable2D(
       state = rememberScrollable2DState { delta ->
           val scrollRangeX = contentSize.width - viewportSize.width
           val scrollRangeY = contentSize.height - viewportSize.height

           val oldX = offset.x
           val oldY = offset.y

           // Dragging right (positive delta) moves the viewport left (decreasing offset)
           val newX = (oldX - delta.x).fastCoerceIn(0f, scrollRangeX.toFloat())
           val newY = (oldY - delta.y).fastCoerceIn(0f, scrollRangeY.toFloat())

           offset = IntOffset(newX.fastRoundToInt(), newY.fastRoundToInt())

           // Return the amount of delta consumed (reverse the sign calculation)
           Offset(oldX - newX, oldY - newY)
       },
   )
   ```

3. **Test Content** (`App.kt:111-143`): A 100x100 grid of colored 20dp squares
   - Content size: 2000dp x 2000dp
   - Provides ample scrollable area to test the functionality

## Platform Status

- **Android**: Working
- **iOS**: Working
- **JVM (Desktop)**: Not working
- **Web (JS)**: Not working
- **Web (Wasm)**: Not working

## Running the Sample

I've stored the run configurations for JVM/Wasm/JS so the IDE can automatically see them.
