import math, random, os
import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageChops, ImageFont

OUT = "art_out"
os.makedirs(OUT, exist_ok=True)
CAVEAT = "app/src/main/res/font/caveat_bold.ttf"
ORANGE = (255, 107, 53)
YELLOW = (255, 209, 102)
CREAM = (244, 237, 224)
BLUE = (76, 125, 255)
MID = (16, 24, 39)
DEEP = (205, 72, 32)
INK = (16, 24, 39)

rng = random.Random(7)


def hexa(c, a=255):
    return (c[0], c[1], c[2], a)


# ---------------------------------------------------------------- ball
def ball(d, light=(0.55, 0.6, 0.6), rim=(1.0, 0.45, 0.2), rot=(0.5, -0.4, 0.3),
         palette=None, ss=3):
    """Shaded volleyball (cube-mapped 3-stripe panels), RGBA d x d."""
    n = d * ss
    ys, xs = np.mgrid[0:n, 0:n]
    x = (xs + 0.5) / n * 2 - 1
    y = -((ys + 0.5) / n * 2 - 1)
    r2 = x * x + y * y
    inside = r2 <= 1
    z = np.sqrt(np.clip(1 - r2, 0, 1))
    N = np.stack([x, y, z], -1)
    a, b, c = rot
    Rx = np.array([[1, 0, 0], [0, math.cos(a), -math.sin(a)], [0, math.sin(a), math.cos(a)]])
    Ry = np.array([[math.cos(b), 0, math.sin(b)], [0, 1, 0], [-math.sin(b), 0, math.cos(b)]])
    Rz = np.array([[math.cos(c), -math.sin(c), 0], [math.sin(c), math.cos(c), 0], [0, 0, 1]])
    P = N @ (Rz @ Ry @ Rx).T
    ax = np.argmax(np.abs(P), -1)
    m = np.take_along_axis(np.abs(P), ax[..., None], -1)[..., 0] + 1e-9
    t = np.take_along_axis(P, ((ax + 1) % 3)[..., None], -1)[..., 0] / m
    o = np.take_along_axis(P, ((ax + 2) % 3)[..., None], -1)[..., 0] / m
    stripe = np.clip(((t + 1) / 2 * 3).astype(int), 0, 2)
    sgn = (np.take_along_axis(P, ax[..., None], -1)[..., 0] > 0).astype(int)
    face = ax * 2 + sgn
    if palette is None:
        palette = [
            [CREAM, ORANGE, CREAM], [ORANGE, CREAM, ORANGE], [CREAM, DEEP, CREAM],
            [ORANGE, CREAM, ORANGE], [CREAM, ORANGE, CREAM], [DEEP, CREAM, DEEP]]
    pal = np.array(palette, dtype=float) / 255.0
    col = pal[face, stripe]
    seam_d = np.minimum(np.abs(np.abs(t) - 1 / 3), 1 - np.maximum(np.abs(t), np.abs(o)))
    seam = np.clip(1 - (seam_d - 0.012) / 0.03, 0, 1) * 0.65
    col = col * (1 - seam[..., None]) + np.array([0.12, 0.08, 0.06]) * seam[..., None]
    L = np.array(light); L = L / np.linalg.norm(L)
    diff = np.clip(N @ L, 0, 1)
    H = L + np.array([0, 0, 1]); H = H / np.linalg.norm(H)
    spec = np.clip(N @ H, 0, 1) ** 38 * 0.55
    rimL = np.array(rim); rimL = rimL / np.linalg.norm(rimL)
    rimv = (np.clip(N @ rimL, 0, 1) ** 3) * (1 - z) ** 1.5
    ao = 0.55 + 0.45 * np.clip((y + 1.1) / 1.6, 0, 1)
    shade = (0.18 + 0.95 * diff) * ao
    rgb = col * shade[..., None] + spec[..., None] + rimv[..., None] * np.array([1.0, 0.45, 0.15]) * 0.9
    rgb = np.clip(rgb, 0, 1)
    alpha = inside.astype(float)
    img = np.dstack([rgb, alpha])
    im = Image.fromarray((img * 255).astype(np.uint8), "RGBA")
    return im.resize((d, d), Image.LANCZOS)


# ---------------------------------------------------------------- doodles
def wobble_path(pts, step=2, amp=2.2, seed=1):
    r = random.Random(seed)
    out = []
    for i in range(len(pts) - 1):
        (x0, y0), (x1, y1) = pts[i], pts[i + 1]
        L = max(1, int(math.hypot(x1 - x0, y1 - y0) / step))
        for k in range(L):
            tt = k / L
            out.append((x0 + (x1 - x0) * tt + r.uniform(-amp, amp) * 0.4,
                        y0 + (y1 - y0) * tt + r.uniform(-amp, amp) * 0.4))
    out.append(pts[-1])
    # smooth jitter
    sm = []
    for i in range(len(out)):
        a = out[max(0, i - 4):i + 5]
        sm.append((sum(p[0] for p in a) / len(a), sum(p[1] for p in a) / len(a)))
    return sm


def bezier(p0, p1, p2, p3, n=60):
    pts = []
    for i in range(n + 1):
        t = i / n
        u = 1 - t
        pts.append((u ** 3 * p0[0] + 3 * u * u * t * p1[0] + 3 * u * t * t * p2[0] + t ** 3 * p3[0],
                    u ** 3 * p0[1] + 3 * u * u * t * p1[1] + 3 * u * t * t * p2[1] + t ** 3 * p3[1]))
    return pts


def stroke(draw, pts, color, w, taper=True):
    n = len(pts)
    for i, (x, y) in enumerate(pts):
        k = 1.0
        if taper:
            f = i / max(1, n - 1)
            k = 0.55 + 0.45 * math.sin(math.pi * min(1, f * 1.15))
        r = w * k / 2
        draw.ellipse([x - r, y - r, x + r, y + r], fill=color)


def arrow(draw, pts, color, w, head=34, seed=3):
    p = wobble_path(pts, seed=seed)
    stroke(draw, p, color, w)
    (x0, y0), (x1, y1) = p[-6], p[-1]
    ang = math.atan2(y1 - y0, x1 - x0)
    for s in (+1, -1):
        a = ang + math.pi + s * 0.5
        tip = [(x1, y1), (x1 + head * math.cos(a), y1 + head * math.sin(a))]
        stroke(draw, wobble_path(tip, seed=seed + s + 5), color, w * 0.95)


def star(draw, cx, cy, R, color, w, seed=9):
    pts = []
    for i in range(11):
        a = -math.pi / 2 + i * math.pi / 5 * 2 * 1.0
        a = -math.pi / 2 + i * 4 * math.pi / 5
        pts.append((cx + R * math.cos(a), cy + R * math.sin(a)))
    stroke(draw, wobble_path(pts, seed=seed, amp=3), color, w, taper=False)


def glow(size, cx, cy, rx, ry, color, alpha, blur):
    g = Image.new("RGBA", size, (0, 0, 0, 0))
    d = ImageDraw.Draw(g)
    d.ellipse([cx - rx, cy - ry, cx + rx, cy + ry], fill=hexa(color, alpha))
    return g.filter(ImageFilter.GaussianBlur(blur))


def add(base, layer):
    """Additive blend of an RGBA light layer onto an RGB(A) base."""
    b = np.asarray(base.convert("RGBA")).astype(float)
    l = np.asarray(layer).astype(float)
    a = l[..., 3:4] / 255
    b[..., :3] = np.clip(b[..., :3] + l[..., :3] * a, 0, 255)
    return Image.fromarray(b.astype(np.uint8), "RGBA")


# ---------------------------------------------------------------- welcome
def welcome():
    W, H = 1080, 2160
    img = Image.new("RGBA", (W, H), hexa(MID))
    # back wall: deep midnight, a touch warmer near the floor
    wall = np.zeros((H, W, 4), np.uint8)
    for yy in range(H):
        t = min(1, yy / 1100)
        wall[yy, :, :3] = (np.array(MID) * (1 - t * 0.25) + np.array([30, 22, 24]) * t * 0.25).astype(np.uint8)
    wall[..., 3] = 255
    img = Image.fromarray(wall, "RGBA")

    # wooden floor: top-down texture then perspective-mapped
    TW, TH = 1600, 2400
    tex = np.zeros((TH, TW, 3), float)
    plank = 100
    r = np.random.default_rng(3)
    for i in range(TW // plank + 1):
        base = np.array([58, 36, 24]) * r.uniform(0.75, 1.1)
        tex[:, i * plank:(i + 1) * plank] = base
    grain = r.normal(0, 1, (TH // 40, TW))
    grain = np.array(Image.fromarray(((grain - grain.min()) / (np.ptp(grain)) * 255).astype(np.uint8)).resize((TW, TH), Image.BICUBIC)) / 255.0
    tex *= (0.82 + 0.3 * grain)[..., None]
    for i in range(TW // plank + 1):
        tex[:, max(0, i * plank - 2):i * plank + 1] *= 0.45
        for j in range(r.integers(1, 3)):
            yj = r.integers(0, TH)
            tex[yj:yj + 3, i * plank:(i + 1) * plank] *= 0.55
    texim = Image.fromarray(np.clip(tex, 0, 255).astype(np.uint8)).convert("RGBA")
    horizon = 980
    # map floor quad: far edge narrow at horizon, near edge wide at bottom
    quad = [(-900, H + 200), (W + 900, H + 200), (W + 120, horizon), (-120, horizon)]
    # PIL perspective needs coefficients mapping output->input; solve
    def coeffs(dst, src):
        A = []; B = []
        for (x, y), (u, v) in zip(dst, src):
            A.append([x, y, 1, 0, 0, 0, -u * x, -u * y]); B.append(u)
            A.append([0, 0, 0, x, y, 1, -v * x, -v * y]); B.append(v)
        return np.linalg.solve(np.array(A, float), np.array(B, float)).tolist()
    src = [(0, TH), (TW, TH), (TW, 0), (0, 0)]
    floor = texim.transform((W, H), Image.PERSPECTIVE, coeffs(quad, src), Image.BICUBIC)
    mask = Image.new("L", (W, H), 0)
    ImageDraw.Draw(mask).polygon([(0, horizon), (W, horizon), (W, H), (0, H)], fill=255)
    # fade floor into darkness toward the back
    fade = np.zeros((H, W), float)
    for yy in range(horizon, H):
        fade[yy] = min(1, (yy - horizon) / 520) ** 1.4
    fl = np.asarray(floor).astype(float)
    fl[..., :3] *= (0.18 + 0.82 * fade)[..., None] * 0.62
    floor = Image.fromarray(np.clip(fl, 0, 255).astype(np.uint8), "RGBA")
    mask = mask.filter(ImageFilter.GaussianBlur(30))
    img.paste(floor, (0, 0), mask)

    # warm light streak: a diagonal band from the top right across the floor
    streak = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    sd = ImageDraw.Draw(streak)
    sd.polygon([(W + 200, 820), (W + 260, 1080), (-200, 1900), (-260, 1560)], fill=hexa(ORANGE, 150))
    streak = streak.filter(ImageFilter.GaussianBlur(70))
    core = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(core).polygon([(W + 200, 900), (W + 220, 990), (-200, 1760), (-220, 1640)], fill=hexa(YELLOW, 110))
    core = core.filter(ImageFilter.GaussianBlur(40))
    fm = Image.fromarray((np.clip(fade, 0, 1) * 255).astype(np.uint8), "L")
    for L_ in (streak, core):
        a = np.asarray(L_).copy()
        a[..., 3] = (a[..., 3].astype(float) * np.asarray(fm) / 255).astype(np.uint8)
        img = add(img, Image.fromarray(a, "RGBA"))
    # light spilling on the wall behind
    img = add(img, glow((W, H), W * 0.9, 760, 520, 260, ORANGE, 70, 140))

    # silhouette legs (left), knee pads, sneakers, rim light
    BX, BY, BD = 560, 1250, 420  # ball box
    legs = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ld = ImageDraw.Draw(legs)
    sil = (8, 10, 16, 255)
    # leg polygons from above the frame of interest down to shoes
    def leg(cx, top, foot, lean):
        # centerline from hip to ankle with thigh, knee, calf, ankle widths
        ys = np.linspace(top, foot, 40)
        prof = np.interp(np.linspace(0, 1, 40), [0, 0.38, 0.5, 0.66, 0.92, 1], [150, 118, 104, 122, 64, 70])
        xs = cx + lean * np.linspace(0, 1, 40) ** 1.3
        calf = np.interp(np.linspace(0, 1, 40), [0, 0.5, 0.66, 0.9, 1], [0, 0, 10, 0, 0])
        left = [(x - w / 2 - c, y) for x, w, c, y in zip(xs, prof, calf, ys)]
        right = [(x + w / 2 + c * 0.4, y) for x, w, c, y in zip(xs, prof, calf, ys)]
        return left + right[::-1], right
    L1, R1 = leg(125, 560, 1440, -10)
    L2, R2 = leg(298, 560, 1400, 10)
    shadow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    shd = ImageDraw.Draw(shadow)
    shd.polygon([(70, 1480), (190, 1470), (-260, 1900), (-420, 1880)], fill=(0, 0, 0, 170))
    shd.polygon([(250, 1440), (350, 1430), (-60, 1820), (-200, 1800)], fill=(0, 0, 0, 150))
    shadow = shadow.filter(ImageFilter.GaussianBlur(28))
    img = Image.alpha_composite(img, shadow)
    ld.polygon(L1, fill=sil)
    ld.polygon(L2, fill=sil)
    # sneakers
    ld.rounded_rectangle([60, 1420, 230, 1490], 30, fill=(14, 16, 24, 255))
    ld.rounded_rectangle([240, 1384, 395, 1446], 28, fill=(14, 16, 24, 255))
    ld.rectangle([60, 1475, 232, 1492], fill=(40, 40, 46, 255))
    ld.rectangle([240, 1432, 397, 1448], fill=(40, 40, 46, 255))
    # knee pads
    ld.ellipse([62, 950, 182, 1080], fill=(13, 15, 23, 255))
    ld.ellipse([240, 940, 360, 1070], fill=(13, 15, 23, 255))
    # shorts hem
    ld.polygon([(20, 560), (390, 560), (380, 700), (230, 690), (205, 660), (190, 700), (30, 700)], fill=(12, 14, 22, 255))
    legs = legs.filter(ImageFilter.GaussianBlur(1.2))
    # rim light on the right edges, facing the streak
    rim = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    rd = ImageDraw.Draw(rim)
    for right in (R1, R2):
        rd.line(right, fill=hexa(ORANGE, 230), width=8, joint="curve")
    rim = rim.filter(ImageFilter.GaussianBlur(3))
    legmask = legs.split()[3]
    rimA = np.asarray(rim).copy()
    rimA[..., 3] = np.minimum(rimA[..., 3], np.asarray(legmask))
    # fade legs into the dark at the top
    la = np.asarray(legs).copy()
    for yy in range(560, 820):
        la[yy, :, 3] = (la[yy, :, 3] * ((yy - 560) / 260)).astype(np.uint8)
        rimA[yy, :, 3] = (rimA[yy, :, 3] * ((yy - 560) / 260)).astype(np.uint8)
    img = Image.alpha_composite(img, Image.fromarray(la, "RGBA"))
    img = add(img, Image.fromarray(rimA, "RGBA"))

    # ball shadow: soft long shadow away from the light + tight contact shadow
    sh = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    sdd = ImageDraw.Draw(sh)
    cx, cy = BX + BD / 2, BY + BD
    sdd.polygon([(cx - 170, cy - 30), (cx + 120, cy + 10), (cx - 520, cy + 330), (cx - 700, cy + 250)], fill=(0, 0, 0, 160))
    sh = sh.filter(ImageFilter.GaussianBlur(45))
    img = Image.alpha_composite(img, sh)
    img = Image.alpha_composite(img, glow((W, H), cx - 10, cy - 8, BD * 0.42, 34, (0, 0, 0), 230, 16))
    # warm bounce light on the floor next to the ball
    img = add(img, glow((W, H), cx + 140, cy - 10, 240, 60, ORANGE, 90, 40))
    b = ball(BD, light=(0.75, 0.55, 0.55), rim=(1.0, 0.1, 0.2), rot=(0.6, -0.5, 0.25))
    img.alpha_composite(b, (BX, BY))
    # reflection of the ball on the varnished floor
    refl = b.transpose(Image.FLIP_TOP_BOTTOM).resize((BD, int(BD * 0.45)))
    ra = np.asarray(refl).copy().astype(float)
    grad = np.linspace(0.32, 0, ra.shape[0])[:, None]
    ra[..., 3] *= grad
    img.alpha_composite(Image.fromarray(ra.astype(np.uint8), "RGBA"), (BX, BY + BD - 6))

    # hand-drawn orange marks
    dd = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(dd)
    arrow(d, bezier((760, 860), (930, 900), (960, 1100), (900, 1215)), hexa(ORANGE), 11, head=40, seed=4)
    star(d, 965, 1240, 48, hexa(YELLOW), 9, seed=11)
    # speed marks to the right of the ball
    for i, (x0, y0) in enumerate([(1000, 1420), (1010, 1480), (995, 1540)]):
        stroke(d, wobble_path([(x0, y0), (x0 + 60, y0 - 6)], seed=20 + i), hexa(ORANGE), 9)
    # loop scribble under the legs line
    img = Image.alpha_composite(img, dd)

    # readability: darken top for the title and bottom for the button
    ov = np.zeros((H, W, 4), np.uint8)
    ov[..., :3] = MID
    a = np.zeros(H)
    for yy in range(H):
        top = max(0, 1 - yy / 900) ** 1.2 * 0.85
        bot = max(0, (yy - 1700) / 460) ** 1.3 * 0.95
        a[yy] = max(top, bot)
    ov[..., 3] = (a[:, None] * 255).astype(np.uint8)
    img = Image.alpha_composite(img, Image.fromarray(ov, "RGBA"))
    # vignette
    yy, xx = np.mgrid[0:H, 0:W]
    v = np.clip(((xx - W / 2) / (W * 0.75)) ** 2 + ((yy - H * 0.6) / (H * 0.7)) ** 2, 0, 1) * 0.45
    vig = np.zeros((H, W, 4), np.uint8); vig[..., 3] = (v * 255).astype(np.uint8)
    img = Image.alpha_composite(img, Image.fromarray(vig, "RGBA"))
    img.convert("RGB").save(f"{OUT}/welcome_hero.jpg", quality=88)
    return img


# ---------------------------------------------------------------- isometric court
S = 2  # supersample
CW = 1080 * S
CS = 30.0 * S
C30, S30 = math.cos(math.radians(30)), math.sin(math.radians(30))
OX, OY = CW / 2, 330 * S


def P(x, y, z=0):
    # x along court length (0..18), y across (0..9), centered on the slab
    x -= 9; y -= 4.5
    return (OX + (x - y) * C30 * CS, OY + 260 * S + (x + y) * S30 * CS - z * CS)


def quad(d, pts3, fill, outline=None, width=0):
    d.polygon([P(*p) for p in pts3], fill=fill, outline=outline)
    if outline and width:
        pp = [P(*p) for p in pts3] + [P(*pts3[0])]
        d.line(pp, fill=outline, width=width, joint="curve")


def box(d, x0, y0, x1, y1, z0, z1, top, left, right):
    quad(d, [(x0, y1, z0), (x1, y1, z0), (x1, y1, z1), (x0, y1, z1)], left)    # front-left face (y=y1)
    quad(d, [(x1, y0, z0), (x1, y1, z0), (x1, y1, z1), (x1, y0, z1)], right)   # front-right face (x=x1)
    quad(d, [(x0, y0, z1), (x1, y0, z1), (x1, y1, z1), (x0, y1, z1)], top)


def tree(d, x, y, h=4.2, r=1.7, seed=0):
    rr = random.Random(seed)
    bx, by = P(x, y, 0)
    tx, ty = P(x, y, h * 0.55)
    d.line([(bx, by), (tx, ty)], fill=(58, 40, 34, 255), width=int(0.35 * CS))
    cx, cy = P(x, y, h)
    R = r * CS
    # canopy: layered blobs, dark teal with a warm-lit side
    for i in range(7):
        ox, oy = rr.uniform(-0.5, 0.5) * R, rr.uniform(-0.45, 0.35) * R
        rad = R * rr.uniform(0.55, 0.8)
        d.ellipse([cx + ox - rad, cy + oy - rad, cx + ox + rad, cy + oy + rad], fill=(24, 58, 60, 255))
    for i in range(5):
        ox, oy = rr.uniform(-0.1, 0.55) * R, rr.uniform(-0.6, 0.1) * R
        rad = R * rr.uniform(0.3, 0.5)
        d.ellipse([cx + ox - rad, cy + oy - rad, cx + ox + rad, cy + oy + rad], fill=(36, 84, 78, 255))
    d.ellipse([cx + 0.25 * R - R * 0.25, cy - 0.55 * R - R * 0.2, cx + 0.25 * R + R * 0.25, cy - 0.55 * R + R * 0.2], fill=(58, 112, 96, 255))


def person(d, x, y, color, arms_up=False, h=1.8):
    fx, fy = P(x, y, 0)
    hx, hy = P(x, y, h)
    w = 0.42 * CS
    # shadow
    d.ellipse([fx - w * 1.1, fy - w * 0.35, fx + w * 1.1, fy + w * 0.35], fill=(0, 0, 0, 90))
    # legs
    d.line([(fx - w * 0.3, fy), (hx - w * 0.2, hy + 0.95 * CS)], fill=(20, 24, 36, 255), width=int(w * 0.5))
    d.line([(fx + w * 0.3, fy - 2), (hx + w * 0.2, hy + 0.95 * CS)], fill=(20, 24, 36, 255), width=int(w * 0.5))
    # torso
    d.rounded_rectangle([hx - w * 0.75, hy + 0.3 * CS, hx + w * 0.75, hy + 1.05 * CS], int(w * 0.35), fill=color)
    # arms
    if arms_up:
        d.line([(hx - w * 0.6, hy + 0.4 * CS), (hx - w * 0.9, hy - 0.35 * CS)], fill=color, width=int(w * 0.38))
        d.line([(hx + w * 0.6, hy + 0.4 * CS), (hx + w * 0.9, hy - 0.35 * CS)], fill=color, width=int(w * 0.38))
    else:
        d.line([(hx - w * 0.7, hy + 0.4 * CS), (hx - w * 0.2, hy + 0.85 * CS)], fill=color, width=int(w * 0.38))
        d.line([(hx + w * 0.7, hy + 0.4 * CS), (hx + w * 0.2, hy + 0.85 * CS)], fill=color, width=int(w * 0.38))
    # head
    d.ellipse([hx - w * 0.42, hy - w * 0.25, hx + w * 0.42, hy + w * 0.6], fill=(236, 196, 160, 255))
    d.chord([hx - w * 0.45, hy - w * 0.3, hx + w * 0.45, hy + w * 0.55], 180, 360, fill=(40, 28, 26, 255))


STAGES = 12


def court_stage(n):
    img = Image.new("RGBA", (CW, CW), (0, 0, 0, 0))
    # sky: night gradient
    sky = np.zeros((CW, CW, 4), np.uint8)
    t = np.linspace(0, 1, CW)[:, None]
    top = np.array([14, 20, 38]); bot = np.array([27, 37, 56])
    sky[..., :3] = (top * (1 - t) + bot * t)[:, None, :].squeeze(1)[:, None, :] if False else (top * (1 - t) + bot * t)[:, None, :].astype(np.uint8).repeat(CW, 1)
    sky[..., 3] = 255
    img = Image.fromarray(sky, "RGBA")
    d = ImageDraw.Draw(img)
    rr = random.Random(5)
    nstars = 30 if n < STAGES else 70
    for i in range(nstars):
        x, y = rr.uniform(0, CW), rr.uniform(0, CW * 0.32)
        r = rr.uniform(1, 2.6) * S
        d.ellipse([x - r, y - r, x + r, y + r], fill=(244, 241, 234, rr.randint(90, 220)))

    # ground island (always there): dark grass/asphalt block under everything
    box(d, -3, -3.2, 21, 12.6, -1.0, 0, (34, 46, 62, 255), (24, 32, 46, 255), (30, 40, 56, 255))
    if n == 0:
        # ghost outline of the court
        pts = [(0, 0), (18, 0), (18, 9), (0, 9), (0, 0)]
        for (a, b), (c, e) in zip(pts, pts[1:]):
            L = math.hypot(c - a, e - b)
            k = int(L / 0.6)
            for i in range(0, k, 2):
                p0 = P(a + (c - a) * i / k, b + (e - b) * i / k)
                p1 = P(a + (c - a) * (i + 1) / k, b + (e - b) * (i + 1) / k)
                d.line([p0, p1], fill=(169, 177, 196, 160), width=3 * S)
    if n >= 1:  # slab around the court
        box(d, -1.8, -1.6, 19.8, 10.6, 0, 0.18, (52, 66, 92, 255), (36, 46, 66, 255), (44, 56, 80, 255))
    if n >= 2:  # orange court
        quad(d, [(0, 0, 0.19), (18, 0, 0.19), (18, 9, 0.19), (0, 9, 0.19)], (232, 96, 46, 255))
        quad(d, [(0, 0, 0.19), (9, 0, 0.19), (9, 9, 0.19), (0, 9, 0.19)], (244, 106, 54, 255))
    if n >= 3:  # white lines
        lw = int(0.12 * CS)
        z = 0.2
        for seg in [[(0, 0), (18, 0)], [(18, 0), (18, 9)], [(18, 9), (0, 9)], [(0, 9), (0, 0)],
                    [(9, 0), (9, 9)], [(6, 0), (6, 9)], [(12, 0), (12, 9)]]:
            d.line([P(seg[0][0], seg[0][1], z), P(seg[1][0], seg[1][1], z)], fill=(250, 246, 238, 255), width=lw)

    back_trees = [(-2.3, -2.4, 0), (5, -2.5, 1), (13, -2.5, 2), (20.3, -2.3, 3)]
    if n >= 8:
        for x, y, s in back_trees:
            tree(d, x, y, seed=s)
    if n >= 9:  # street lamp at the back right corner
        lx, ly = 20.0, 0.5
        b0, b1 = P(lx, ly, 0), P(lx, ly, 6.2)
        d.line([b0, b1], fill=(58, 66, 86, 255), width=int(0.22 * CS))
        arm = P(lx - 1.1, ly + 0.4, 6.2)
        d.line([b1, arm], fill=(58, 66, 86, 255), width=int(0.16 * CS))
        d.ellipse([arm[0] - 0.42 * CS, arm[1] - 0.15 * CS, arm[0] + 0.42 * CS, arm[1] + 0.3 * CS], fill=(255, 226, 150, 255))

    if n >= 4:  # posts
        for y in (-0.8, 9.8):
            a, b = P(9, y, 0.2), P(9, y, 2.6)
            d.line([a, b], fill=(214, 220, 232, 255), width=int(0.2 * CS))
            d.ellipse([a[0] - 0.22 * CS, a[1] - 0.1 * CS, a[0] + 0.22 * CS, a[1] + 0.12 * CS], fill=(110, 118, 140, 255))
    if n >= 5:  # net
        top, bot = 2.43, 1.45
        net = Image.new("RGBA", img.size, (0, 0, 0, 0))
        nd = ImageDraw.Draw(net)
        nd.polygon([P(9, -0.8, top), P(9, 9.8, top), P(9, 9.8, bot), P(9, -0.8, bot)], fill=(16, 24, 39, 120))
        k = 0
        y = -0.8
        while y <= 9.8:
            nd.line([P(9, y, top), P(9, y, bot)], fill=(244, 241, 234, 120), width=S)
            y += 0.35
        z = bot
        while z <= top:
            nd.line([P(9, -0.8, z), P(9, 9.8, z)], fill=(244, 241, 234, 120), width=S)
            z += 0.2
        nd.line([P(9, -0.8, top), P(9, 9.8, top)], fill=(250, 248, 242, 255), width=int(0.12 * CS))
        img = Image.alpha_composite(img, net)
        d = ImageDraw.Draw(img)

    if n >= 10:  # teammates
        person(d, 4.5, 3.0, (76, 125, 255, 255), arms_up=True)
        person(d, 13.5, 6.0, (255, 209, 102, 255))
        person(d, 6.8, 7.2, (76, 125, 255, 255))
    if n >= 6:  # the ball, with a little shadow on the court
        bx, by = P(11.5, 3.5, 0.2)
        d.ellipse([bx - 0.4 * CS, by - 0.12 * CS, bx + 0.4 * CS, by + 0.12 * CS], fill=(0, 0, 0, 90))
        bd = int(0.85 * CS)
        bimg = ball(bd, ss=2)
        img.alpha_composite(bimg, (int(bx - bd / 2), int(by - bd * 1.75)))
        d = ImageDraw.Draw(img)
    if n >= 7:  # bench along the front side
        box(d, 3, 11.2, 8.5, 11.9, 0.18, 0.75, (176, 112, 70, 255), (120, 74, 48, 255), (150, 94, 60, 255))
        box(d, 3.2, 11.75, 8.3, 11.9, 0.75, 1.5, (176, 112, 70, 255), (120, 74, 48, 255), (150, 94, 60, 255))
    if n >= 11:  # bag and bottle on the bench side
        box(d, 9.5, 11.2, 10.8, 11.9, 0.18, 0.75, (76, 125, 255, 255), (48, 86, 196, 255), (60, 104, 226, 255))
        bx, by = P(11.6, 11.5, 0.18)
        bt = P(11.6, 11.5, 1.0)
        d.rounded_rectangle([bx - 0.18 * CS, bt[1], bx + 0.18 * CS, by], int(0.1 * CS), fill=(255, 209, 102, 255))
    if n >= 8:  # front tree, corner
        tree(d, 20.3, 11.6, h=3.6, r=1.4, seed=9)

    img = img.convert("RGBA")
    # lighting
    if n >= 9:
        lamp = P(18.9, 0.9, 0)
        img = add(img, glow(img.size, lamp[0], lamp[1] + 40 * S, 7 * CS, 3.4 * CS, (255, 196, 110), 120, 60 * S))
        arm = P(18.9, 0.9, 6.2)
        img = add(img, glow(img.size, arm[0], arm[1], 1.2 * CS, 1.2 * CS, (255, 226, 150), 200, 18 * S))
    if n >= 12:  # string lights across the back, and a warm glow over the court
        d = ImageDraw.Draw(img)
        a, b = P(-2.3, -2.4, 3.6), P(20.3, -2.3, 3.8)
        pts = []
        for i in range(41):
            t = i / 40
            x = a[0] + (b[0] - a[0]) * t
            y = a[1] + (b[1] - a[1]) * t + math.sin(math.pi * t) * 70 * S
            pts.append((x, y))
        d.line(pts, fill=(20, 24, 34, 255), width=2 * S)
        bulbs = Image.new("RGBA", img.size, (0, 0, 0, 0))
        bd_ = ImageDraw.Draw(bulbs)
        for i, (x, y) in enumerate(pts[1:-1:2]):
            c = (255, 209, 102) if i % 2 else (255, 140, 80)
            bd_.ellipse([x - 5 * S, y - 3 * S, x + 5 * S, y + 7 * S], fill=hexa(c))
        img = Image.alpha_composite(img, bulbs)
        img = add(img, bulbs.filter(ImageFilter.GaussianBlur(10 * S)))
        c = P(9, 4.5, 0)
        img = add(img, glow(img.size, c[0], c[1], 13 * CS, 6 * CS, ORANGE, 55, 80 * S))
    out = img.resize((1080, 1080), Image.LANCZOS).convert("RGB")
    out.save(f"{OUT}/court_stage_{n:02d}.jpg", quality=86)
    return out


# ---------------------------------------------------------------- today
def today_art():
    W, H = 1000, 640
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    ink = (16, 24, 39, 255)
    # sketchy net
    l, r, t, b = 90, 910, 230, 420
    for x in range(l, r + 1, 34):
        stroke(d, wobble_path([(x, t), (x + 4, b)], seed=x, amp=2), (16, 24, 39, 90), 3, taper=False)
    for y in range(t, b + 1, 30):
        stroke(d, wobble_path([(l, y), (r, y + 3)], seed=y, amp=2), (16, 24, 39, 90), 3, taper=False)
    stroke(d, wobble_path([(l - 10, t - 4), (r + 10, t + 2)], seed=2, amp=3), ink, 9, taper=False)
    for x in (l, r):
        stroke(d, wobble_path([(x, t - 30), (x + 3, H - 30)], seed=x + 1, amp=2), ink, 10, taper=False)
    # orange floor sweep
    stroke(d, wobble_path(bezier((0, 600), (300, 520), (650, 560), (1000, 470)), seed=5, amp=2), hexa(ORANGE), 16)
    # pencil shadow and ball above the net
    bx, by, bd = 560, 30, 190
    img.alpha_composite(ball(bd, light=(0.5, 0.7, 0.6), rim=(1, 0.2, 0.3)), (bx, by))
    # motion marks + star + arrow toward the top-left label
    for i in range(3):
        y = by + 60 + i * 34
        stroke(d, wobble_path([(bx + bd + 25, y), (bx + bd + 85, y - 8)], seed=60 + i), hexa(ORANGE), 8)
    star(d, 180, 120, 34, hexa(ORANGE), 7, seed=12)
    arrow(d, bezier((330, 70), (420, 30), (500, 60), (540, 110)), hexa(ORANGE), 8, head=26, seed=8)
    img.save(f"{OUT}/today_art.png", optimize=True)


def done_art():
    W, H = 800, 560
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # hand-drawn circle around a ball resting on an orange line
    cx, cy = 400, 270
    loop = [(cx + 250 * math.cos(a) + 6 * math.sin(5 * a), cy + 220 * math.sin(a)) for a in np.linspace(-0.6, 2 * math.pi + 0.1, 120)]
    stroke(d, wobble_path(loop, seed=3, amp=3), hexa(ORANGE), 12)
    stroke(d, wobble_path(bezier((60, 470), (300, 430), (520, 460), (760, 420)), seed=5), hexa(ORANGE), 14)
    img = Image.alpha_composite(img, glow((W, H), 400, 437, 95, 14, (16, 24, 39), 110, 8))
    d = ImageDraw.Draw(img)
    img.alpha_composite(ball(210, light=(0.5, 0.7, 0.6), rim=(1, 0.2, 0.3)), (295, 225))
    # check mark doodle
    stroke(d, wobble_path([(560, 120), (610, 175), (720, 40)], seed=6, amp=3), (16, 24, 39, 255), 16)
    star(d, 140, 120, 40, hexa(YELLOW), 9, seed=14)
    img.save(f"{OUT}/done_art.png", optimize=True)


if __name__ == "__main__":
    import sys
    what = sys.argv[1:] or ["welcome", "court", "today"]
    if "welcome" in what:
        welcome()
    if "court" in what:
        for n in range(STAGES + 1):
            court_stage(n)
    if "today" in what:
        today_art(); done_art()
