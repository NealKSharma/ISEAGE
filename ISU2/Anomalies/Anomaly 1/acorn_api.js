const FLAG = "CTF{nutty_nut_secured}";
const SECRET_KEY = "NUT4EVER";

export default {
    async fetch(request) {
        if (request.method === "OPTIONS") {
            return new Response(null, {
                headers: {
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Methods": "POST",
                    "Access-Control-Allow-Headers": "Content-Type"
                }
            });
        }

        const url = new URL(request.url);
        if (url.pathname !== "/flag") return json({ error: "Nothing here." }, 404);
        if (request.method !== "POST") return json({ error: "Wrong method." }, 405);
        const ct = request.headers.get("Content-Type") || "";
        if (!ct.includes("application/json")) return json({ error: "Wrong content type." }, 400);

        let body;
        try { body = await request.json(); } 
        catch { return json({ error: "Invalid JSON." }, 400); }
        if (body.key !== SECRET_KEY) return json({ error: "Wrong key." }, 403);

        return json({ flag: FLAG });
    }
}

function json(data, status = 200) {
    return new Response(JSON.stringify(data), {
        status,
        headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" }
    });
}