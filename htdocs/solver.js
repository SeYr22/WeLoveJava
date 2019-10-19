class PointD {
    constructor (x, y) {
        this.x = x;
        this.y = y;
    }
}
var initialT = 100000, endT = 0.01, mul = 0.9998;
var dist = [];
class PlanPath {
    constructor() {}
    static init_matrix (p) {
        let n = p.length;
        dist = new Array(n);
        for (let i = 0; i < n; i++) dist[i] = new Array(n);
        for (let i = 0; i < n; i++) {
            for (let j = 0; j < n; j++) {
                dist[i][j] = Math.sqrt((p[i].x - p[j].x) * (p[i].x - p[j].x) + (p[i].y - p[j].y) * (p[i].y - p[j].y)) * 111320.0;
            }
        }
    }
    static calc_res (res) {
        let ans = dist[res[0]][res[res.length - 1]];
        for (let i = 1; i < res.length; i++) {
            ans += dist[res[i - 1]][res[i]];
        }
        return ans;
    }
    static nextInt (max) {
        return Math.floor(Math.random() * Math.floor(max));
    }
    static nextDouble () {
        return Math.random();
    }
    static init (p) {
        let n = p.length;
        let res = new Array(n);
        //alert("cur " + p.length);
        for (let i = 0; i < n; i++) {
            res[i] = i;
        }
        if (n > 1500) return res;
        this.init_matrix(p);
        for (let i = 0; i < n; i++) {
            let to = this.nextInt(n);
            [res[i], res[to]] = [res[to], res[i]];
        }
       // let kol = 0;
        let cur = this.calc_res(res);
        for (let t = initialT; t > endT; t *= mul) {
           // ++kol;
            let x = this.nextInt(n);
            let y = this.nextInt(n);
            if (x > y) {
                [x, y] = [y, x];
            }
            let ans = new Array(n);
            for (let j = 0; j < n; j++) ans[j] = res[j];
            let x1 = x, y1 = y;
            while (x1 < y1) {
                [ans[x1], ans[y1]] = [ans[y1], ans[x1]];
                ++x1;
                --y1;
            }
            let ene = this.calc_res(ans);
            if (ene < cur) {
                cur = ene;
                while (x < y) {
                    [res[x], res[y]] = [res[y], res[x]];
                    ++x;
                    --y;
                }
            }
            else {
                let pr = Math.exp(-(ene - cur) / t);
                if (pr >= this.nextDouble()) {
                    cur = ene;
                    while (x < y) {
                        [res[x], res[y]] = [res[y], res[x]];
                        ++x;
                        --y;
                    }
                }
            }
        }
        let answer = new Array(n);
        for (let i = 0; i < n; i++) {
            answer[i] = p[res[i]];
        }
       // alert(kol);
        return answer;
    }
}
var h = 1, opt;
class SalesmanPrepare {
    constructor () {}
    static area (a, b, c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }
    static intersect (a, b, c, d) {
        if (a > b)
        {
            [a, b] = [b, a];
        }
        if (c > d)
        {
            [c, d] = [d, c];
        }
        return Math.max(a, c) <= Math.min(b, d);
    }
    static intersect_segments (a, b, c, d) {
        return this.intersect(a.x, b.x, c.x, d.x)
            && this.intersect(a.y, b.y, c.y, d.y)
            && this.area(a, b, c) * this.area(a, b, d) <= 0
            && this.area(c, d, a) * this.area(c, d, b) <= 0;
    }
    static inPolygon (p, x, sz) {
        let n = p.length;
        let p1 = new PointD(x.x - sz.x / 2.0, x.y - sz.y / 2.0),
            p2 = new PointD(x.x + sz.x / 2.0, x.y - sz.y / 2.0),
            p3 = new PointD(x.x - sz.x / 2.0, x.y + sz.y / 2.0),
            p4 = new PointD(x.x + sz.x / 2.0, x.y + sz.y / 2.0);
        for (let i = 0; i < n; i++) {
            let to = (i == n - 1) ? 0 : i + 1;
            if (this.intersect_segments(p[i], p[to], p1, p2) ||
                this.intersect_segments(p[i], p[to], p1, p3) ||
                this.intersect_segments(p[i], p[to], p3, p4) ||
                this.intersect_segments(p[i], p[to], p2, p4)) return true;
        }
        let cnt = false;
        let s = new PointD(x.x, 10000000);
        for (let i = 0; i < n; i++) {
            let to = (i == n - 1) ? 0 : i + 1;
            if (this.intersect_segments(p[i], p[to], x, s)) cnt ^= true;
        }
        return cnt;
    }
    static get_points (p, lft, rght, sz) {
        let ans = [];
        if (rght.x - lft.x <= sz.x && rght.y - lft.y <= sz.y)
        {
            ans.push(new PointD(lft.x + (rght.x - lft.x) / 2.0, lft.y + (rght.y - lft.y) / 2.0));
        }
        else {
            for (let x = lft.x; x < rght.x && ans.length <= 1500; x += sz.x)
            {
                let tox = Math.min(x + sz.x, rght.x);
                let mid = x + (tox - x) / 2.0;
                for (let y = lft.y; y < rght.y && ans.length <= 1500; y += sz.y)
                {
                    let toy = Math.min(rght.y, y + sz.y);
                    let mid_y = y + (toy - y) / 2.0;
                    if (this.inPolygon(p, new PointD(mid, mid_y), sz))
                        ans.push(new PointD(mid, mid_y));
                }
            }
        }
        return ans;
    }
    static get_sz (height) {
        let centr = new Point3(0, 0, height * 1000.0);
        let lft_bot = MathTransform.getMatrixPointOnGround(centr, "leftBottomCorner");
        let rght_bot = MathTransform.getMatrixPointOnGround(centr, "rightBottomCorner");
        let rght_top = MathTransform.getMatrixPointOnGround(centr, "rightTopCorner");
        return new PointD(Math.sqrt((lft_bot.x - rght_bot.x) * (lft_bot.x - rght_bot.x) + (lft_bot.y - rght_bot.y) * (lft_bot.y - rght_bot.y)) / 111320.0,
                         Math.sqrt((rght_top.x - rght_bot.x) * (rght_top.x - rght_bot.x) + (rght_top.y - rght_bot.y) * (rght_top.y - rght_bot.y)) / 71500.0);
    }
    static get_len (p) {
        let res = Math.sqrt((p[0].x - p[p.length - 1].x) * (p[0].x - p[p.length - 1].x) + (p[0].y - p[p.length - 1].y) * (p[0].y - p[p.length - 1].y));
        for (let i = 1; i < p.length; i++) {
            res += Math.sqrt((p[i].x - p[i - 1].x) * (p[i].x - p[i - 1].x) + (p[i].y - p[i - 1].y) * (p[i].y - p[i - 1].y));
        }
        return res * 111320.0;
    }
    static find (p, minHeight, maxHeight, on_go, on_photo) {
        let n = p.length;
        let lft = new PointD(100000000, 100000000), rght = new PointD(-100000000, -100000000);
        for (let i = 1; i < n; i++)
        {
            lft.x = Math.min(lft.x, p[i].x);
            lft.y = Math.min(lft.y, p[i].y);
            rght.x = Math.max(rght.x, p[i].x);
            rght.y = Math.max(rght.y, p[i].y);
        }
        let optimalPath = 1000000000000000;
        let ans = [];
        let d = (maxHeight - minHeight) / 10.0;
        let o = 0;
        for (let cur = minHeight; cur <= maxHeight && o < 10; cur += d) {
            let P = [];
            for (let j = 1; j < n; j++) {
                P.push(p[j]);
            }
            let P1 = this.get_points(P, lft, rght, this.get_sz(cur));
            P1.push(p[0]);
            let can = PlanPath.init(P1);
            let ene = (this.get_len(can) + cur * 2000.0) / 1000.0 * on_go + can.length * on_photo;
            if (ene < optimalPath) {
                h = cur;
                optimalPath = ene;
                ans = can;
            }
            ++o;
        }
        opt = optimalPath;
        return ans;
    }
}
var cm_height = 0, cm_width = 0, focu = 0;
/*class Matrices {

     constructor(yaw){
        this.yawMatrix = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];
        this.yawMatrix[0][0] = Math.cos(yaw);
        this.yawMatrix[0][1] = Math.sin(yaw);
        this.yawMatrix[0][2] = 0;
        this.yawMatrix[0][3] = 0;

        this.yawMatrix[1][0] = -Math.sin(yaw);
        this.yawMatrix[1][1] = Math.cos(yaw);
        this.yawMatrix[1][2] = 0;
        this.yawMatrix[1][3] = 0;

        this.yawMatrix[2][0] = 0;
        this.yawMatrix[2][1] = 0;
        this.yawMatrix[2][2] = 1;
        this.yawMatrix[2][3] = 0;

        this.yawMatrix[3][0] = 0;
        this.yawMatrix[3][1] = 0;
        this.yawMatrix[3][2] = 0;
        this.yawMatrix[3][3] = 1;
    }
}*/
class Point3 {
    constructor (x, y, z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
class MathTransform {
    constructor() {}
    static getMatrixPointOnGround(centerMatrix, point) {
        let groundMatrix = new Point3(0, 0, 0);

        let airMatrix = [[0], [0], [0], [0]];
        let focusMatrix = this.getMatrixPointOnAir(centerMatrix, "focus");

        airMatrix = this.getMatrixPointOnAir(centerMatrix, point);
        let directVector = this.getDirectingVector(airMatrix, focusMatrix);

        let param = -airMatrix[2][0] / directVector.z;
        groundMatrix.x = airMatrix[0][0] + directVector.x * param;
        groundMatrix.y = airMatrix[1][0] + directVector.y * param;

        return groundMatrix;
    }
    static getMatrixPointOnAir(center, point) {
        let cornerMatrix = [[0], [0], [0], [0]];

        if(point == "rightTopCorner"){
            cornerMatrix[0][0] = center.x + this.convertMmToM(cm_width);
            cornerMatrix[1][0] = center.y + this.convertMmToM(cm_height);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "leftTopCorner"){
            cornerMatrix[0][0] = center.x - this.convertMmToM(cm_width);
            cornerMatrix[1][0] = center.y + this.convertMmToM(cm_height);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "rightBottomCorner"){
            cornerMatrix[0][0] = center.x + this.convertMmToM(cm_width);
            cornerMatrix[1][0] = center.y - this.convertMmToM(cm_height);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }
       
        if(point == "leftBottomCorner"){
            cornerMatrix[0][0] = center.x - this.convertMmToM(cm_width);
            cornerMatrix[1][0] = center.y - this.convertMmToM(cm_height);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "focus"){
            cornerMatrix[0][0] = center.x;
            cornerMatrix[1][0] = center.y;
            cornerMatrix[2][0] = center.z - this.convertMmToM(focu);
            cornerMatrix[3][0] = 1;
        }

        return  cornerMatrix;
    }
    static convertMmToM(mm){
        return mm / 1000.0;
    }

    static getDirectingVector(pointMatrix, focus){ 
        let vector = new Point3(0, 0, 0);

        vector.x = pointMatrix[0][0] - focus[0][0];
        vector.y = pointMatrix[1][0]- focus[1][0];
        vector.z = pointMatrix[2][0] - focus[2][0];

        return vector;
    }
}