# ChromaHunt 🎯

> Color-histogram matching and localization of objects against a chroma-key background.

**ChromaHunt** is a Java application that detects and localises multiple objects in a scene using **HSV color histogram matching** and **connected-component analysis**. Each reference object is photographed against a green-screen background; ChromaHunt learns its color signature and finds every instance of it in the scene, drawing bounding boxes around all matches.

---

## How It Works

The pipeline runs in three stages:

### 1. Reference Histogram Extraction (`readImageRGB`)
The reference image (the object you want to find) is processed to build an **HSV color histogram**:
- Pixels with pure green (`G = 255`) are treated as background and masked out.
- For every non-background pixel, the RGB values are converted to **HSV** (Hue 0–360°, Saturation 0–100%, Value 0–100%).
- A hue histogram of 361 bins is accumulated, capturing the color distribution of the target object.
- The total pixel count of the masked object is stored and used to compute relative hue frequencies.

### 2. Color Mask Generation (`readImageRGBREF`)
The scene image is scanned pixel-by-pixel. Each pixel is converted to HSV and compared against the reference histogram using **adaptive saturation thresholds** — rarer hues in the reference require the scene pixel to have higher saturation to be accepted. This produces a binary mask where `'1'` marks pixels that match the object's color profile.

The adaptive thresholds work as follows:

| Reference hue frequency (%) | Required saturation |
|---|---|
| 0.01 – 0.09 | > 100% (effectively disabled — too rare) |
| 0.09 – 0.3  | > 90% |
| 0.4 – 0.7   | > 60% |
| 0.7 – 1.0   | > 30% |
| 1.0 – 1.4   | > 20% |
| 1.4 – 2.0   | > 15% |
| 2.0 – 3.0   | > 10% |
| 3.0 – 5.0   | ≥ 10% |
| 5.0 – 6.0   | ≥ 55% |
| 6.0 – 7.0   | ≥ 45% |
| 7.0 – 15.0  | ≥ 55% |
| ≥ 15.0      | ≥ 65% |

### 3. Connected-Component Detection (`numIslands` / `dfs`)
The binary mask is treated as a grid, and all connected regions of matching pixels are found using **iterative DFS** (8-directional connectivity, including diagonals). Each connected component is analysed for:
- Total pixel count
- Bounding box: `(minX, maxX, minY, maxY)`

Components with fewer than **1,400 matching pixels** are discarded as noise. Each surviving component is a detected object instance, and a **green bounding box** (4px thick) is drawn onto the output image.

---

## Pipeline Diagram

```
  object1.rgb … objectN.rgb              InputImage.rgb
  (chroma-key green BG)                  (scene to search)
           │                                    │
           ▼                                    ▼
   Mask green background             Convert each pixel
   per reference object                   to HSV
           │                                    │
           ▼                                    ▼
   Build HSV hue histogram        Compare hue + saturation
   per reference object            against each reference
           │                                    │
           └──────────────┬─────────────────────┘
                          ▼
                 Binary color mask
                 (per object match)
                          │
                          ▼
           Connected-component analysis
              (iterative DFS, 8-dir)
                          │
                          ▼
            Filter: size ≥ 1,400 px
                          │
                          ▼
          Draw green bounding boxes
          for all objects × all matches
                          │
                          ▼
               Display result (Swing)
```

---

## Usage

### Compile

```bash
javac ImageDisplay.java
```

### Run

```bash
./ColorDetect InputImage.rgb object1.rgb object2.rgb … objectN.rgb
```

Or equivalently with `java` directly:

```bash
java ImageDisplay InputImage.rgb object1.rgb object2.rgb … objectN.rgb
```

| Argument | Description |
|---|---|
| `InputImage.rgb` | The **scene** image to search through |
| `object1.rgb … objectN.rgb` | One or more **reference** objects (each on a chroma-key green background) |

ChromaHunt iterates through every reference object, builds its color histogram, and localises all matching regions in the scene — in a single pass. All detections are overlaid on the same output image.

**Single object example:**
```bash
./ColorDetect scene.rgb ball.rgb
```

**Multiple objects example:**
```bash
./ColorDetect scene.rgb ball.rgb cup.rgb book.rgb
```

A Swing window will open displaying the scene with **green bounding boxes** drawn around every detected instance of every reference object.

---

## Image Format

Images must be in **raw RGB format** (`.rgb`), stored in planar layout:
- All **R** values first (width × height bytes)
- Then all **G** values
- Then all **B** values

Default resolution: **640 × 480** (configurable via `width` and `height` fields in `ImageDisplay`).

### Preparing the Reference Image

The reference object must be photographed or rendered against a **pure green background** (`R=any, G=255, B=any`). The green background is automatically masked out during histogram extraction.

---

## Output

- **Console** — For each detected object, prints:
  - Island number
  - Pixel count
  - Bounding box coordinates (minX, maxX, minY, maxY)

- **GUI window** — The scene image with **green rectangles** (4px border) drawn around every detected object instance.

**Example console output:**
```
Island 1:
Number of '1's: 3842
Min X: 112, Max X: 198
Min Y: 205, Max Y: 310

Island 2:
Number of '1's: 2107
Min X: 420, Max X: 497
Min Y: 88,  Max Y: 176
```

---

## Configuration

All thresholds are hardcoded constants in `ImageDisplay.java`:

| Constant | Location | Default | Description |
|---|---|---|---|
| `width` / `height` | fields | 640 / 480 | Input image dimensions |
| Min island size | `numIslands()` | 1400 px | Minimum pixel count to keep a detection |
| Bounding box thickness | `readImageRGBREF()` | 4 px | Border width of drawn rectangles |

---

## Requirements

- Java 8 or later
- No external libraries — uses only the Java standard library (`java.awt`, `javax.swing`, `java.io`)

---

## Limitations

- **Color-only matching** — no shape, texture, or edge information is used. Objects with similar colors to the background will produce false positives.
- **Fixed image size** — the resolution must be set manually before compilation if your images differ from 640×480.
- **Green-screen reference only** — the reference image must use pure green (`G=255`) as the background mask color.
- **No rotation or scale invariance** — the detector finds pixels by color alone; it does not account for object orientation or size changes.# object-detection
matching, and localization of objects against a chroma‑key background
Run as:
./ColorDetect InputImage.rgb object1.rgb object2.rgb … objectN.rgb
