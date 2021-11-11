const url = new URL(window.location);
let sort = url.searchParams.get("sort");


if (sort!=null){
    document.getElementById('sort_by').value = sort;
} else {
    sort = sessionStorage.sort;
    sort = sort==null?"status":sort;
    sessionStorage.sort=sort;
    insertParam("sort",sort);
}

function insertParam(key, value) {
    key = encodeURIComponent(key);
    value = encodeURIComponent(value);

    const kvp = document.location.search.substr(1).split('&');
    let i=0;

    for(; i<kvp.length; i++){
        if (kvp[i].startsWith(key + '=')) {
            let pair = kvp[i].split('=');
            pair[1] = value;
            kvp[i] = pair.join('=');
            break;
        }
    }

    if(i >= kvp.length){
        kvp[kvp.length] = [key,value].join('=');
    }
    document.location.search = kvp.join('&');
}

function onSortChange(event){
    sessionStorage.sort=event.target.value;
    insertParam("sort",sessionStorage.sort)
}