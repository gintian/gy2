/**
 * 不要修改这个文件
 *
 *
 */
(function() {
    if (typeof Object.id == "undefined") {
        var id = 0;
        Object.id = function(o) {
            if (typeof o.__uniqueid == "undefined") {
                Object.defineProperty(o, "__uniqueid", {
                    value: ++id,
                    enumerable: false,
                    // This could go either way, depending on your 
                    // interpretation of what an "id" is
                    writable: false
                });
            }
            return o.__uniqueid;
        };
    }
})();

function HashTable(){
    var size = 0;
    var entry = new Object();

    this.add = function(key , value){
        if(!this.containsKey(key)){
            size ++ ;
        }
        entry[key] = value;
    }

    this.getValue = function(key){
        return this.containsKey(key) ? entry[key] : null;
    }

    this.remove = function(key){
        if(this.containsKey(key) && (delete entry[key])){
            size --;
        }
    }

    this.containsKey = function(key){
        return (key in entry);
    }

    this.containsValue = function(value){
        for(var prop in entry){
            if(entry[prop] == value){
                return true;
            }
        }
        return false;
    }

    this.getValues = function(){
        var values = new Array();
        for(var prop in entry){
            values.push(entry[prop]);
        }
        return values;
    }

    this.getKeys = function (){
        var keys = new Array();
        for(var prop in entry){
            keys.push(prop);
        }
        return keys;
    }

    this.getSize = function(){
        return size;
    }

    this.clear = function(){
        size = 0;
        entry = new Object();
    }
}