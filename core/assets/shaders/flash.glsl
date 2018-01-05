#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

vec3 liteout(vec3 color){
    color.r = 255.0/255.0;
    color.g = 255.0/255.0;
    color.b = 255.0/255.0;
    return color;
}

void main() {
        vec3 color = liteout(texture2D(u_texture, v_texCoords).rgb * v_color);
        gl_FragColor = vec4(color, texture2D(u_texture, v_texCoords).a);
}